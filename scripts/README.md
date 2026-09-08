# 시연 데이터

`seed-demo-scenarios.sql`은 발표용 세 계정을 만든다. V4/V8의 기존 `demo@roommade.com` 데이터에는 영향을 주지 않는다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/seed-demo-scenarios.sql
```

시드 실행 후 프론트 로그인 화면에서 아래 계정으로 로그인해 시나리오를 전환한다.

| 이메일 | 용도 |
| --- | --- |
| `demo01@roommade.com` | 준비도 55점(RIR 35점 + 보증금 20점). 가구 선택권 3개를 모두 미사용 상태로 제공한다. |
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
