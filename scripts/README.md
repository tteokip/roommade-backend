# 시연 데이터

지원금 메인의 시연 정책은 V20 마이그레이션 적용 후 `seed-demo-policy-features.sql`로 계정 초기화 없이 설정할 수 있다. 전체 계정 시드에도 같은 설정이 포함된다.

- demo01·02: 청년주택드림청약통장 → 청년안심주택 공급(매입) → 청년 매입임대주택 사업
- demo03: 미혼청년 주거급여 분리지급 → 기초청년 주거급여(임차급여)

메인 전용 `/api/youth-policies/featured`는 설정된 실제 정책을 자격·지역·마감일 필터 없이 표시한다. 일반 계정은 기존 맞춤 조회를 사용하며 전체 정책 목록과 상세 내용은 기존 동작을 유지한다. 정책 동기화 후 시드 결과의 정책명이 NULL이면 해당 고유번호를 확인한다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/seed-demo-policy-features.sql
```

`seed-demo-scenarios.sql`은 발표용 세 계정을 만든다. V4/V8의 기존 `demo@roommade.com` 데이터에는 영향을 주지 않는다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/seed-demo-scenarios.sql
```

시드 실행 후 프론트 로그인 화면에서 아래 계정으로 로그인해 시나리오를 전환한다.

| 이메일 | 용도 |
| --- | --- |
| `demo01@roommade.com` | 월급 250만 원, 예상 월세 80만 원, 모은 보증금 1,000만 원/목표 3,000만 원. 준비도 55.5점(RIR 40.5점 + 보증금 15점). 가구 선택권 3개를 모두 미사용 상태로 제공한다. |
| `demo02@roommade.com` | 준비도 90점(RIR 45점 + 보증금 45점). 기본 가구와 가구 선택권을 모두 해금한 상태에서 집 비교 완료(100점)와 입주 확정을 시연한다. |
| `demo03@roommade.com` | 입주 완료, 생활비·신용관리 퀴즈와 웜 오크 가구 7종이 배치된 방을 제공한다. 전날 일일 챌린지 1단계 성공(20코인 지급) 기록과 38만 원/50만 원 비상금을 제공한다. 코지 코티지 가구 구매 시연용으로 520코인도 보유한다. |

세 계정의 공통 비밀번호는 `demo1234`이다.

전체 시연 계정을 지우려면 다음을 실행한다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/reset-demo-scenarios.sql
```

시연 직전 상태를 확인하려면 다음을 실행한다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/verify-demo-scenarios.sql
```

오늘의 퀴즈는 전역 데이터라 시드 실행 시 V6의 첫 번째 신용관리 OX 문항으로 고정된다. 발표용 로컬/DEV DB에서만 실행한다.
