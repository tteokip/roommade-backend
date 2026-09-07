# 시연 데이터

`seed-demo-scenarios.sql`은 발표용 세 계정을 만든다. V4/V8의 기존 `demo@roommade.com` 데이터에는 영향을 주지 않는다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/seed-demo-scenarios.sql
```

마지막 `SELECT` 결과의 `user_id`를 프론트엔드 `.env.local`의 `VITE_DEV_USER_ID`에 넣어 계정을 전환한다.

```env
VITE_DEV_USER_ID=3
```

| 이메일 | 용도 |
| --- | --- |
| `demo01@roommade.com` | 준비도 55점(RIR 45점 + 보증금 10점). 집 비교 완료 후 65점이 되며 60점 가구 선택권을 시연한다. |
| `demo02@roommade.com` | 확정 매물과 월세가 연결된 입주 예정(D-3) 상태다. |
| `demo03@roommade.com` | 입주 완료, 생활비·신용관리 퀴즈와 웜 오크 가구 7종이 배치된 방을 제공한다. 코지 코티지 가구 구매 시연용으로 500코인도 보유한다. |

현재 앱은 인증 대신 `X-User-Id`를 임시 사용하므로 이메일/비밀번호 로그인은 아직 제공하지 않는다.

전체 시연 계정을 지우려면 다음을 실행한다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/reset-demo-scenarios.sql
```

시연 직전 상태를 확인하려면 다음을 실행한다.

```bash
docker compose exec -T mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/verify-demo-scenarios.sql
```

오늘의 퀴즈는 전역 데이터라 시드 실행 시 V6의 첫 번째 신용관리 OX 문항으로 고정된다. 발표용 로컬/DEV DB에서만 실행한다.
