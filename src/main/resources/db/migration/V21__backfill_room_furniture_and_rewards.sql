-- 조회 API에서 제거된 지연 초기화 로직을 대신해 기존 사용자의 방 데이터를 보정한다.
INSERT IGNORE INTO user_furniture (user_id, furniture_id, is_placed)
SELECT
    u.id,
    f.id,
    NOT EXISTS (
        SELECT 1
        FROM user_furniture placed_uf
        INNER JOIN furniture placed_f ON placed_f.id = placed_uf.furniture_id
        WHERE placed_uf.user_id = u.id
          AND placed_uf.is_placed = TRUE
          AND placed_f.category_id = f.category_id
    )
FROM users u
CROSS JOIN furniture f
WHERE f.furniture_type = 'BASIC'
  AND f.unlock_score = 0
  AND f.active = TRUE;

-- 입주를 확정한 기존 사용자는 모든 기본 가구를 보유해야 한다.
INSERT IGNORE INTO user_furniture (user_id, furniture_id, is_placed)
SELECT
    ip.user_id,
    f.id,
    NOT EXISTS (
        SELECT 1
        FROM user_furniture placed_uf
        INNER JOIN furniture placed_f ON placed_f.id = placed_uf.furniture_id
        WHERE placed_uf.user_id = ip.user_id
          AND placed_uf.is_placed = TRUE
          AND placed_f.category_id = f.category_id
    )
FROM independence_progress ip
CROSS JOIN furniture f
WHERE ip.move_in_date IS NOT NULL
  AND f.furniture_type = 'BASIC'
  AND f.active = TRUE;

-- 아직 입주 전인 기존 사용자의 현재 준비도에 맞춰 누락된 단계 보상을 생성한다.
INSERT IGNORE INTO furniture_reward (user_id, reward_stage)
SELECT readiness.user_id, stages.reward_stage
FROM (
    SELECT
        up.user_id,
        ROUND(
            CASE
                WHEN up.monthly_rent_limit * 100 <= up.monthly_income * 30 THEN 45
                WHEN up.monthly_rent_limit * 100 >= up.monthly_income * 50 THEN 0
                ELSE (
                    (up.monthly_income * 50 - up.monthly_rent_limit * 100)
                    * 100 / (up.monthly_income * 20)
                ) * 0.45
            END,
            2
        )
        + ROUND(
            CASE
                WHEN ip.current_deposit >= up.deposit_limit THEN 45
                ELSE ip.current_deposit * 100 / up.deposit_limit * 0.45
            END,
            2
        )
        + CASE WHEN ip.house_compare_completed_at IS NULL THEN 0 ELSE 10 END
            AS readiness_score
    FROM user_profiles up
    INNER JOIN independence_progress ip ON ip.user_id = up.user_id
    WHERE ip.move_in_date IS NULL
      AND up.monthly_income > 0
      AND up.monthly_rent_limit > 0
      AND up.deposit_limit > 0
      AND ip.current_deposit >= 0
) readiness
CROSS JOIN (
    SELECT 15 AS reward_stage
    UNION ALL SELECT 30
    UNION ALL SELECT 45
    UNION ALL SELECT 60
    UNION ALL SELECT 75
) stages
WHERE readiness.readiness_score >= stages.reward_stage;
