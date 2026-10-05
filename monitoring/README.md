# RoomMade 모니터링

백엔드는 `/metrics`에서 Prometheus 형식의 메트릭을 제공합니다.

- JVM/프로세스 메트릭: Prometheus Java client의 `JvmMetrics`
- HTTP 요청 수: `roommade_http_requests_total`
- HTTP 지연 시간: `roommade_http_request_duration_seconds`

경로 라벨은 숫자와 UUID 식별자를 `{id}`로 정규화해 시계열 수가 무제한으로 늘어나는 문제를 막습니다.

## 로컬 실행

백엔드를 `http://localhost:8080`에서 먼저 실행한 뒤, 이 디렉터리에서 실행합니다.

```bash
docker compose up -d
```

- Prometheus: <http://localhost:9090>
- Grafana: <http://localhost:3000> (기본 계정 `admin` / `admin`)

기본 Grafana 계정은 로컬 개발용입니다. 공유/운영 환경에서는 실행 전 `GRAFANA_ADMIN_USER`,
`GRAFANA_ADMIN_PASSWORD` 환경 변수를 반드시 설정합니다.

## 배포 연동

`prometheus/prometheus.yml`의 `targets`를 배포 환경에서 Prometheus가 접근 가능한 백엔드 주소로 바꿉니다.
`/metrics` 엔드포인트는 외부 공개하지 말고, Prometheus 네트워크 또는 IP만 접근하게 보안 그룹/리버스 프록시에서 제한하세요.

## 첫 Grafana 패널

Grafana에서 Prometheus 데이터 소스와 `RoomMade / RoomMade Overview` 대시보드가 자동 등록됩니다.
필요하면 아래 PromQL로 패널을 추가할 수 있습니다.

| 패널 | PromQL |
| --- | --- |
| 초당 요청 수 | `sum(rate(roommade_http_requests_total[5m]))` |
| 5xx 비율 | `sum(rate(roommade_http_requests_total{status=~"5.."}[5m])) / sum(rate(roommade_http_requests_total[5m]))` |
| p95 응답 시간 | `histogram_quantile(0.95, sum by (le) (rate(roommade_http_request_duration_seconds_bucket[5m])))` |
| JVM 힙 사용률 | `sum(jvm_memory_used_bytes{area="heap"}) / sum(jvm_memory_max_bytes{area="heap"})` |
