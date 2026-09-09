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

-- 1번 계정: 월급 250만·월세 80만, RIR 40.5점 + 보증금 15점 = 55.5점.
-- 미사용 가구 선택권 3개를 확인한다.
SELECT
    u.email,
    up.monthly_rent_limit * 100.0 / up.monthly_income AS rir_percent,
    CASE
        WHEN up.monthly_rent_limit * 100 <= up.monthly_income * 30 THEN 45
        WHEN up.monthly_rent_limit * 100 >= up.monthly_income * 50 THEN 0
        ELSE (up.monthly_income * 50 - up.monthly_rent_limit * 100) * 45.0
             / (up.monthly_income * 20)
    END AS rir_score,
    LEAST(ip.current_deposit * 45.0 / up.deposit_limit, 45) AS deposit_score,
    ip.current_deposit,
    ip.house_compare_completed_at,
    (
        SELECT COUNT(*)
        FROM furniture_reward fr
        WHERE fr.user_id = u.id
          AND fr.claimed_at IS NULL
    ) AS pending_furniture_rewards
FROM users u
INNER JOIN user_profiles up ON up.user_id = u.id
INNER JOIN independence_progress ip ON ip.user_id = u.id
WHERE u.email = 'demo01@roommade.com';

-- 2번 계정: 집 비교 전 90점, 모든 가구 선택권 사용 완료 상태를 확인한다.
SELECT
    u.email,
    up.monthly_rent_limit * 100.0 / up.monthly_income AS rir_percent,
    ip.current_deposit,
    ip.house_compare_completed_at,
    (
        SELECT COUNT(*)
        FROM furniture_reward fr
        WHERE fr.user_id = u.id
          AND fr.claimed_at IS NULL
    ) AS pending_furniture_rewards
FROM users u
INNER JOIN user_profiles up ON up.user_id = u.id
INNER JOIN independence_progress ip ON ip.user_id = u.id
WHERE u.email = 'demo02@roommade.com';

-- 3번 계정: 확정 매물의 월세, 비상금, 전날 챌린지 1단계·20코인 지급 기록을 확인한다.
SELECT
    u.email,
    h.location AS confirmed_house,
    h.monthly_rent AS house_monthly_rent,
    lr.monthly_rent AS living_monthly_rent,
    ef.current_amount AS emergency_current_amount,
    ef.target_amount AS emergency_target_amount,
    ip.move_in_date,
    ip.moved_in_at,
    yesterday_cost.total_amount AS yesterday_spending,
    yesterday_level.level AS yesterday_challenge_level,
    yesterday_level.reward_coin AS yesterday_reward_coin,
    yesterday_challenge.closed_at AS yesterday_challenge_closed_at
FROM users u
INNER JOIN independence_progress ip ON ip.user_id = u.id
INNER JOIN houses h ON h.id = ip.confirmed_house_id
INNER JOIN living_rents lr ON lr.user_id = u.id
INNER JOIN emergency_funds ef ON ef.user_id = u.id
INNER JOIN daily_living_costs yesterday_cost
    ON yesterday_cost.user_id = u.id
    AND yesterday_cost.spending_date = CURDATE() - INTERVAL 1 DAY
INNER JOIN daily_challenges yesterday_challenge ON yesterday_challenge.daily_living_cost_id = yesterday_cost.id
INNER JOIN challenge_levels yesterday_level ON yesterday_level.id = yesterday_challenge.achieved_level_id
WHERE u.email = 'demo03@roommade.com';

-- 오늘의 퀴즈가 신용관리 문항으로 고정됐는지 확인한다.
SELECT dq.quiz_date, qq.question
FROM daily_quizzes dq
INNER JOIN quiz_questions qq ON qq.id = dq.quiz_question_id
WHERE dq.quiz_date = CURDATE();
