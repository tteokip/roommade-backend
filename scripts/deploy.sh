#!/usr/bin/env bash

set -euo pipefail

usage() {
    cat <<'EOF'
Usage:
  sudo bash scripts/deploy.sh \
    --war /path/to/roommade-backend-0.0.1-SNAPSHOT.war \
    --webapps-dir /opt/tomcat9/webapps \
    --service-name tomcat9 \
    --health-url http://127.0.0.1:8080/health

Required options:
  --war PATH           Path to the WAR file to deploy.
  --webapps-dir PATH   Tomcat webapps directory. The WAR is deployed as roommade.war.
  --service-name NAME  systemd service name for Tomcat.
  --health-url URL     Health endpoint expected to return HTTP 200.

Optional options:
  --backup-dir PATH    Directory for previous WAR backups.
                       Default: <webapps-dir>/../backups
  --tomcat-user USER   Owner of roommade.war. Default: User= in the systemd unit.
  --tomcat-group GROUP Group of roommade.war. Default: Group= in the systemd unit.
  --health-timeout N   Seconds to wait for the health endpoint. Default: 30
  --help               Show this help message.
EOF
}

fail() {
    echo "배포 실패: $*" >&2
    exit 1
}

WAR_PATH=""
WEBAPPS_DIR=""
SERVICE_NAME=""
HEALTH_URL=""
BACKUP_DIR=""
TOMCAT_USER=""
TOMCAT_GROUP=""
HEALTH_TIMEOUT=30

while [[ $# -gt 0 ]]; do
    case "$1" in
        --war)
            WAR_PATH="${2:-}"
            shift 2
            ;;
        --webapps-dir)
            WEBAPPS_DIR="${2:-}"
            shift 2
            ;;
        --service-name)
            SERVICE_NAME="${2:-}"
            shift 2
            ;;
        --health-url)
            HEALTH_URL="${2:-}"
            shift 2
            ;;
        --backup-dir)
            BACKUP_DIR="${2:-}"
            shift 2
            ;;
        --tomcat-user)
            TOMCAT_USER="${2:-}"
            shift 2
            ;;
        --tomcat-group)
            TOMCAT_GROUP="${2:-}"
            shift 2
            ;;
        --health-timeout)
            HEALTH_TIMEOUT="${2:-}"
            shift 2
            ;;
        --help)
            usage
            exit 0
            ;;
        *)
            usage >&2
            fail "알 수 없는 옵션입니다: $1"
            ;;
    esac
done

[[ "${EUID}" -eq 0 ]] || fail "Tomcat 파일 교체와 재시작을 위해 sudo로 실행하세요."
[[ -n "${WAR_PATH}" ]] || fail "--war 옵션은 필수입니다."
[[ -n "${WEBAPPS_DIR}" ]] || fail "--webapps-dir 옵션은 필수입니다."
[[ -n "${SERVICE_NAME}" ]] || fail "--service-name 옵션은 필수입니다."
[[ -n "${HEALTH_URL}" ]] || fail "--health-url 옵션은 필수입니다."
[[ -f "${WAR_PATH}" ]] || fail "WAR 파일을 찾을 수 없습니다: ${WAR_PATH}"
[[ -d "${WEBAPPS_DIR}" ]] || fail "Tomcat webapps 디렉터리를 찾을 수 없습니다: ${WEBAPPS_DIR}"
WEBAPPS_DIR="$(realpath "${WEBAPPS_DIR}")" || fail "Tomcat webapps 디렉터리 경로를 확인할 수 없습니다: ${WEBAPPS_DIR}"
[[ "${WEBAPPS_DIR}" != "/" ]] || fail "Tomcat webapps 디렉터리에 /를 사용할 수 없습니다."
[[ "${HEALTH_TIMEOUT}" =~ ^[1-9][0-9]*$ ]] || fail "--health-timeout은 1 이상의 정수여야 합니다."

command -v curl >/dev/null 2>&1 || fail "curl이 설치되어 있어야 합니다."
command -v systemctl >/dev/null 2>&1 || fail "systemctl을 사용할 수 있는 환경에서 실행하세요."
command -v getent >/dev/null 2>&1 || fail "getent를 사용할 수 있는 Linux 환경에서 실행하세요."

if [[ -z "${TOMCAT_USER}" ]]; then
    if ! TOMCAT_USER="$(systemctl show "${SERVICE_NAME}" --property=User --value)"; then
        fail "systemd 서비스를 찾을 수 없습니다: ${SERVICE_NAME}"
    fi
    TOMCAT_USER="${TOMCAT_USER:-root}"
fi
id -u "${TOMCAT_USER}" >/dev/null 2>&1 || fail "Tomcat 실행 사용자를 찾을 수 없습니다: ${TOMCAT_USER}"

if [[ -z "${TOMCAT_GROUP}" ]]; then
    if ! TOMCAT_GROUP="$(systemctl show "${SERVICE_NAME}" --property=Group --value)"; then
        fail "systemd 서비스 그룹 정보를 읽을 수 없습니다: ${SERVICE_NAME}"
    fi
    TOMCAT_GROUP="${TOMCAT_GROUP:-$(id -gn "${TOMCAT_USER}")}"
fi
getent group "${TOMCAT_GROUP}" >/dev/null 2>&1 || fail "Tomcat 실행 그룹을 찾을 수 없습니다: ${TOMCAT_GROUP}"

if [[ -z "${BACKUP_DIR}" ]]; then
    BACKUP_DIR="$(dirname "${WEBAPPS_DIR}")/backups"
fi

APP_NAME="roommade"
TARGET_WAR="${WEBAPPS_DIR}/${APP_NAME}.war"
EXPLODED_APP_DIR="${WEBAPPS_DIR}/${APP_NAME}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
TEMP_WAR="${WEBAPPS_DIR}/.${APP_NAME}-${TIMESTAMP}.war"
BACKUP_WAR=""
DEPLOYED=false
TOMCAT_STOPPED=false

set_war_permissions() {
    chown "${TOMCAT_USER}:${TOMCAT_GROUP}" "$1" || return 1
    chmod 0644 "$1" || return 1
}

wait_for_health() {
    local deadline=$((SECONDS + HEALTH_TIMEOUT))

    while (( SECONDS < deadline )); do
        local status_code
        status_code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "${HEALTH_URL}" || true)"
        if [[ "${status_code}" == "200" ]]; then
            return 0
        fi

        sleep 1
    done

    return 1
}

rollback() {
    local exit_code="$1"

    trap - ERR
    echo "배포 중 오류가 발생했습니다. 이전 WAR로 복구를 시도합니다." >&2
    rm -f "${TEMP_WAR}" || true

    if [[ "${DEPLOYED}" == true ]]; then
        systemctl stop "${SERVICE_NAME}" || true
        rm -rf "${EXPLODED_APP_DIR}" || echo "경고: 이전 배포 디렉터리 삭제에 실패했습니다: ${EXPLODED_APP_DIR}" >&2

        if [[ -n "${BACKUP_WAR}" && -f "${BACKUP_WAR}" ]]; then
            if cp -p "${BACKUP_WAR}" "${TARGET_WAR}"; then
                if ! set_war_permissions "${TARGET_WAR}"; then
                    echo "롤백 WAR의 소유권 또는 권한 복구에 실패했습니다." >&2
                fi
                echo "이전 WAR 복구 완료: ${BACKUP_WAR}" >&2
            else
                echo "경고: 이전 WAR 복구용 복사에 실패했습니다: ${BACKUP_WAR}" >&2
            fi
        else
            if rm -f "${TARGET_WAR}"; then
                echo "기존 WAR가 없어 새 ${APP_NAME}.war를 제거했습니다." >&2
            else
                echo "경고: 새 ${APP_NAME}.war 제거에 실패했습니다: ${TARGET_WAR}" >&2
            fi
        fi
    fi

    if [[ "${TOMCAT_STOPPED}" == true ]]; then
        systemctl start "${SERVICE_NAME}" || true

        if [[ "${DEPLOYED}" == true ]]; then
            if wait_for_health; then
                echo "이전 버전 롤백 후 health check 성공: ${HEALTH_URL}" >&2
            else
                echo "이전 버전 롤백 후에도 health check에 실패했습니다. 인프라 상태를 확인하세요." >&2
            fi
        fi
    fi

    exit "${exit_code}"
}

trap 'rollback $?' ERR

mkdir -p "${BACKUP_DIR}"

systemctl stop "${SERVICE_NAME}"
TOMCAT_STOPPED=true
echo "Tomcat 중지 완료: ${SERVICE_NAME}"

if [[ -f "${TARGET_WAR}" ]]; then
    BACKUP_WAR="${BACKUP_DIR}/${APP_NAME}-${TIMESTAMP}.war"
    cp -p "${TARGET_WAR}" "${BACKUP_WAR}"
    echo "기존 WAR 백업 완료: ${BACKUP_WAR}"
fi

rm -rf "${EXPLODED_APP_DIR}"
cp -p "${WAR_PATH}" "${TEMP_WAR}"
mv -f "${TEMP_WAR}" "${TARGET_WAR}"
DEPLOYED=true
set_war_permissions "${TARGET_WAR}"
echo "새 WAR 배치 완료: ${TARGET_WAR}"

systemctl start "${SERVICE_NAME}"
echo "Tomcat 시작 요청 완료: ${SERVICE_NAME}"

if wait_for_health; then
    echo "배포 성공: ${HEALTH_URL} -> HTTP 200"
    exit 0
fi

echo "배포 실패: ${HEALTH_TIMEOUT}초 안에 health check가 성공하지 않았습니다: ${HEALTH_URL}" >&2
rollback 1
