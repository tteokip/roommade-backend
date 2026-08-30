/* =========================================================
   생활비 현황 시연용 데모 데이터 (demo@roommade.com)
   ========================================================= */

INSERT INTO daily_living_costs (user_id, spending_date, total_amount)
WITH RECURSIVE date_range AS (
    SELECT DATE('2026-08-01') AS spending_date
    UNION ALL
    SELECT spending_date + INTERVAL 1 DAY
    FROM date_range
    WHERE spending_date < DATE('2026-08-30')
)
SELECT
    (SELECT id FROM users WHERE email = 'demo@roommade.com'),
    spending_date,
    12000 + (DAYOFWEEK(spending_date) * 2500)
FROM date_range;

INSERT INTO daily_living_costs (user_id, spending_date, total_amount)
WITH RECURSIVE date_range AS (
    SELECT DATE('2026-07-01') AS spending_date
    UNION ALL
    SELECT spending_date + INTERVAL 1 DAY
    FROM date_range
    WHERE spending_date < DATE('2026-07-30')
)
SELECT
    (SELECT id FROM users WHERE email = 'demo@roommade.com'),
    spending_date,
    10000 + (DAYOFWEEK(spending_date) * 2000)
FROM date_range;

INSERT INTO monthly_living_costs (user_id, `year_month`, total_amount)
SELECT id, '2026-07', 1320000
FROM users
WHERE email = 'demo@roommade.com';

INSERT INTO monthly_living_costs (user_id, `year_month`, total_amount)
SELECT id, '2026-06', 1400000
FROM users
WHERE email = 'demo@roommade.com';
