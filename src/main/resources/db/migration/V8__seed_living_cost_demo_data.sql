/* =========================================================
   생활비 현황 시연용 데모 데이터 (demo@roommade.com)

   날짜를 고정값으로 박지 않고 CURDATE() 기준 상대 계산으로 채운다.
   서비스 로직이 "이번 달"/"지난달"을 항상 오늘 날짜 기준으로 다시 계산하기 때문에,
   날짜를 하드코딩하면 달이 바뀌는 순간 조회 결과가 비어버린다.
   ========================================================= */

INSERT INTO daily_living_costs (user_id, spending_date, total_amount)
WITH RECURSIVE date_range AS (
    SELECT DATE_FORMAT(CURDATE(), '%Y-%m-01') AS spending_date
    UNION ALL
    SELECT spending_date + INTERVAL 1 DAY
    FROM date_range
    WHERE spending_date < CURDATE()
)
SELECT
    (SELECT id FROM users WHERE email = 'demo@roommade.com'),
    spending_date,
    12000 + (DAYOFWEEK(spending_date) * 2500)
FROM date_range;

INSERT INTO daily_living_costs (user_id, spending_date, total_amount)
WITH RECURSIVE date_range AS (
    SELECT DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') AS spending_date
    UNION ALL
    SELECT spending_date + INTERVAL 1 DAY
    FROM date_range
    WHERE spending_date < LAST_DAY(CURDATE() - INTERVAL 1 MONTH)
)
SELECT
    (SELECT id FROM users WHERE email = 'demo@roommade.com'),
    spending_date,
    10000 + (DAYOFWEEK(spending_date) * 2000)
FROM date_range;

INSERT INTO monthly_living_costs (user_id, `year_month`, total_amount)
SELECT id, DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m'), 1320000
FROM users
WHERE email = 'demo@roommade.com';

INSERT INTO monthly_living_costs (user_id, `year_month`, total_amount)
SELECT id, DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m'), 1400000
FROM users
WHERE email = 'demo@roommade.com';
