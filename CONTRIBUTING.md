# Contributing Guide

## 이슈 작성 규칙

- 작업을 시작하기 전에 목적에 맞는 이슈 템플릿으로 이슈를 작성합니다.
- 이슈 제목에는 작업 내용을 명확하게 표현합니다.
- 담당자(Assignee)와 적절한 라벨을 지정합니다.
- 하나의 이슈에는 하나의 목적만 담습니다.

## 브랜치 전략

| 브랜치 | 역할 | 반영 방법 |
|---|---|---|
| `main` | 배포 가능한 안정 버전 | `develop`에서 Pull Request로 반영 |
| `develop` | 다음 버전을 위한 통합 개발 | 작업 브랜치에서 Pull Request로 반영 |
| 작업 브랜치 | 이슈 단위 작업 | `develop`에서 분기하고 병합 후 삭제 |

현재는 운영 배포를 하지 않습니다. 추후 배포 환경이 준비되면 `main`을 배포 기준
브랜치로 사용하며, 배포 자동화의 실행 조건과 운영 절차는 별도로 문서화합니다.

### 브랜치 네이밍 규칙

| 타입 | 패턴 | 예시 |
|---|---|---|
| 기능 개발 | `feat/{설명}` | `feat/user-signup` |
| 버그 수정 | `fix/{설명}` | `fix/login-validation` |
| 문서 작업 | `docs/{설명}` | `docs/update-readme` |
| 설정·의존성 | `chore/{설명}` | `chore/init-project` |
| 스타일 수정 | `style/{설명}` | `style/format-code` |
| 리팩토링 | `refactor/{설명}` | `refactor/user-service` |
| 테스트 | `test/{설명}` | `test/add-user-service` |
| 성능 개선 | `perf/{설명}` | `perf/optimize-query` |
| CI/CD 설정 | `ci/{설명}` | `ci/add-build-workflow` |

- 브랜치 설명에는 영어 소문자와 하이픈(`-`)을 사용합니다.
- 하나의 브랜치는 하나의 이슈 또는 작업을 기준으로 합니다.

## 커밋 메시지 컨벤션

[Conventional Commits](https://www.conventionalcommits.org/) 형식을 따릅니다.

```text
<type>: <subject>

[body]

[footer]
```

| 타입 | 설명 |
|---|---|
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `docs` | 문서 변경 |
| `style` | 동작 변경이 없는 코드 스타일 수정 |
| `refactor` | 기능 변경이 없는 코드 구조 개선 |
| `test` | 테스트 추가 또는 수정 |
| `chore` | 빌드 설정, 의존성 및 기타 작업 |
| `perf` | 성능 개선 |
| `ci` | CI/CD 설정 변경 |

예시:

```text
feat: 회원가입 API 추가

Closes #12
```

## Pull Request 규칙

- Pull Request에는 하나의 목적만 담습니다.
- 작업 브랜치의 기본 대상(base)은 `develop`입니다.
- `develop`을 안정 버전으로 반영할 때는 `main`을 대상으로 Pull Request를 만듭니다.
- 제목은 `[#이슈번호] type: 작업 내용` 형식을 사용합니다.
- 관련 이슈를 본문에 연결하고, 변경 사항과 검증 방법을 작성합니다.
- 최소 1명의 승인을 받은 뒤 병합합니다.
- 기본 병합 방식은 **Squash and Merge**입니다.

## 코드 리뷰 규칙

- 동작의 정확성, 테스트 여부, 가독성과 컨벤션 준수 여부를 함께 확인합니다.
- 수정 요청에는 이유와 가능한 대안을 함께 작성합니다.
- 필수 수정이 아닌 사소한 의견에는 `nit:` 접두어를 사용합니다.
- 모든 리뷰 의견에 반영 결과 또는 반영하지 않은 이유를 답변한 뒤 대화를 해결합니다.

