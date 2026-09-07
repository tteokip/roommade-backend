-- seed-demo-scenarios.sql 실행 뒤 시연 전 확인용 쿼리

SELECT
    u.id AS user_id,
    u.email,
    up.name,
    up.monthly_income,
    up.monthly_rent_limit,
    up.deposit_limit,
    ip.current_deposit,
    ip.move_in_date,
    ip.moved_in_at,
    cw.balance AS coin_balance,
    (
        SELECT COUNT(*)
        FROM user_furniture uf
        INNER JOIN furniture f ON f.id = uf.furniture_id
        WHERE uf.user_id = u.id
          AND f.furniture_type = 'SHOP'
    ) AS owned_shop_furniture_count,
    (
        SELECT COUNT(*)
        FROM user_furniture uf
        INNER JOIN furniture f ON f.id = uf.furniture_id
        WHERE uf.user_id = u.id
          AND f.furniture_type = 'SHOP'
          AND uf.is_placed = TRUE
    ) AS placed_shop_furniture_count,
    (
        SELECT COUNT(*)
        FROM furniture_reward fr
        WHERE fr.user_id = u.id
          AND fr.claimed_at IS NULL
    ) AS pending_furniture_rewards
FROM users u
INNER JOIN user_profiles up ON up.user_id = u.id
INNER JOIN independence_progress ip ON ip.user_id = u.id
INNER JOIN coin_wallets cw ON cw.user_id = u.id
WHERE u.email IN (
    'demo01@roommade.com',
    'demo02@roommade.com',
    'demo03@roommade.com'
)
ORDER BY FIELD(
    u.email,
    'demo01@roommade.com',
    'demo02@roommade.com',
    'demo03@roommade.com'
);

-- preparing: RIR 30% = 45점, 보증금 약 22.22% = 10점, 집 비교 점수는 0점이어야 한다.
SELECT
    u.email,
    up.monthly_rent_limit * 100.0 / up.monthly_income AS rir_percent,
    CASE
        WHEN up.monthly_rent_limit * 100 <= up.monthly_income * 30 THEN 45
        WHEN up.monthly_rent_limit * 100 >= up.monthly_income * 50 THEN 0
        ELSE (up.monthly_income * 50 - up.monthly_rent_limit * 100) * 45.0
             / (up.monthly_income * 20)
    END AS rir_score,
    ip.current_deposit,
    ip.house_compare_completed_at
FROM users u
INNER JOIN user_profiles up ON up.user_id = u.id
INNER JOIN independence_progress ip ON ip.user_id = u.id
WHERE u.email = 'demo01@roommade.com';

-- transition/living: 확정 매물의 월세가 실제 생활 월세로 연결됐는지 확인한다.
SELECT
    u.email,
    h.location AS confirmed_house,
    h.monthly_rent AS house_monthly_rent,
    lr.monthly_rent AS living_monthly_rent,
    ip.move_in_date,
    ip.moved_in_at
FROM users u
INNER JOIN independence_progress ip ON ip.user_id = u.id
INNER JOIN houses h ON h.id = ip.confirmed_house_id
INNER JOIN living_rents lr ON lr.user_id = u.id
WHERE u.email IN ('demo02@roommade.com', 'demo03@roommade.com');

-- 오늘의 퀴즈가 신용관리 문항으로 고정됐는지 확인한다.
SELECT dq.quiz_date, qq.question
FROM daily_quizzes dq
INNER JOIN quiz_questions qq ON qq.id = dq.quiz_question_id
WHERE dq.quiz_date = CURDATE();
