-- RoomMade 발표 시연용 계정 3종 생성 스크립트
--
-- 대상: 로컬/DEV 데이터베이스 전용. 운영 DB에서는 절대 실행하지 않는다.
-- 실행: docker compose exec -T mysql sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/seed-demo-scenarios.sql
--
-- 재실행 안전: 발표 시연 계정과 이전 명칭의 시연 계정만 초기화한 뒤 다시 만든다.
-- V4/V8의 기존 demo@roommade.com 데이터는 건드리지 않는다.

-- Git Bash·PowerShell 파이프 입력에서도 한글 시드 값이 깨지지 않도록 연결 문자셋을 고정한다.
SET NAMES utf8mb4;
SET @today = CURDATE();
SET @preparing_email = 'demo01@roommade.com';
SET @transition_email = 'demo02@roommade.com';
SET @living_email = 'demo03@roommade.com';
SET @legacy_preparing_email = 'demo-preparing@roommade.local';
SET @legacy_transition_email = 'demo-transition@roommade.local';
SET @legacy_living_email = 'demo-living@roommade.local';

START TRANSACTION;

-- 기존 시연 계정 데이터만 의존성 역순으로 제거한다.
DELETE dc
FROM daily_challenges dc
INNER JOIN daily_living_costs dlc ON dlc.id = dc.daily_living_cost_id
INNER JOIN users u ON u.id = dlc.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE uqa
FROM user_quiz_attempts uqa
INNER JOIN users u ON u.id = uqa.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE pa
FROM preference_answers pa
INNER JOIN house_comparisons hc ON hc.id = pa.comparison_id
INNER JOIN users u ON u.id = hc.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE lr
FROM living_rents lr
INNER JOIN users u ON u.id = lr.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE ef
FROM emergency_funds ef
INNER JOIN users u ON u.id = ef.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE fr
FROM furniture_reward fr
INNER JOIN users u ON u.id = fr.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE uf
FROM user_furniture uf
INNER JOIN users u ON u.id = uf.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE cw
FROM coin_wallets cw
INNER JOIN users u ON u.id = cw.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE dlc
FROM daily_living_costs dlc
INNER JOIN users u ON u.id = dlc.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE mlc
FROM monthly_living_costs mlc
INNER JOIN users u ON u.id = mlc.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

-- confirmed_house_id가 houses를 참조하므로 매물보다 먼저 진행 상태를 지운다.
DELETE ip
FROM independence_progress ip
INNER JOIN users u ON u.id = ip.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE h
FROM houses h
INNER JOIN house_comparisons hc ON hc.id = h.comparison_id
INNER JOIN users u ON u.id = hc.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE hc
FROM house_comparisons hc
INNER JOIN users u ON u.id = hc.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE up
FROM user_profiles up
INNER JOIN users u ON u.id = up.user_id
WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

DELETE FROM users
WHERE email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

-- 시연 계정 공통 비밀번호는 demo1234이다.
INSERT INTO users (email, password_hash) VALUES
(@preparing_email, '$2a$10$JX1u9v/j8.yoLngLGPhh1ukcarDGzb3.3qI2Gblt6QYGLKKDFMzvC'),
(@transition_email, '$2a$10$JX1u9v/j8.yoLngLGPhh1ukcarDGzb3.3qI2Gblt6QYGLKKDFMzvC'),
(@living_email, '$2a$10$JX1u9v/j8.yoLngLGPhh1ukcarDGzb3.3qI2Gblt6QYGLKKDFMzvC');

-- 1번 계정은 RIR 35점 + 보증금 20점, 2번 계정은 RIR 45점 + 보증금 45점으로 구성한다.
INSERT INTO user_profiles (
    user_id, name, birth_date, monthly_income,
    workplace_road_address, workplace_detail_address,
    deposit_limit, monthly_rent_limit
)
SELECT id, '김룸메', '1999-05-18', 3000000,
       '서울특별시 영등포구 여의대로 24', '10층', 30000000, 1033333
FROM users
WHERE email = @preparing_email
UNION ALL
SELECT id, '김룸메', '1999-05-18', 3000000,
       '서울특별시 영등포구 여의대로 24', '10층', 30000000, 900000
FROM users
WHERE email = @transition_email
UNION ALL
SELECT id, '김룸메', '1999-05-18', 3000000,
       '서울특별시 영등포구 여의대로 24', '10층', 30000000, 900000
FROM users
WHERE email = @living_email;

-- 1) 독립 전: RIR 35점 + 보증금 약 44.44%(20점), 집 비교 점수 0 = 준비도 55.
--    15·30·45점 가구 선택권 3개를 미사용 상태로 시연한다.
INSERT INTO independence_progress (user_id, current_deposit)
SELECT id, 13333333 FROM users WHERE email = @preparing_email;

-- 2) 독립 전: RIR 45점 + 보증금 45점 = 준비도 90.
--    집 비교를 완료해 100점을 만들고, 이어서 입주 확정까지 진행한다.
INSERT INTO independence_progress (
    user_id, current_deposit
)
SELECT id, 30000000
FROM users WHERE email = @transition_email;

-- 3) 독립 후: 입주 완료 및 생활비/비상금/퀴즈 시연용 상태.
INSERT INTO house_comparisons (user_id, status, completed_at)
SELECT id, 'COMPLETED', NOW() - INTERVAL 14 DAY
FROM users WHERE email = @living_email;
SET @living_comparison_id = LAST_INSERT_ID();

INSERT INTO houses (
    comparison_id, house_type, location, deposit, monthly_rent, maintenance_fee,
    area, station_walk_minutes, commute_min_minutes, commute_max_minutes,
    floor_type, room_structure, option_type
) VALUES
(@living_comparison_id, 'A', '서울 영등포구 당산동', 10000000, 900000, 70000,
 29.70, 6, 32, 41, '중층', '오픈형 원룸', '에어컨 포함'),
(@living_comparison_id, 'B', '서울 영등포구 문래동', 5000000, 980000, 80000,
 26.40, 9, 38, 49, '고층', '분리형 원룸', '냉장고 포함');
SET @living_house_a_id = (
    SELECT id FROM houses WHERE comparison_id = @living_comparison_id AND house_type = 'A'
);

INSERT INTO independence_progress (
    user_id, current_deposit, house_compare_completed_at, confirmed_house_id, move_in_date, moved_in_at
)
SELECT id, 30000000, NOW() - INTERVAL 14 DAY, @living_house_a_id,
       @today - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY
FROM users WHERE email = @living_email;

INSERT INTO living_rents (user_id, monthly_rent)
SELECT id, 900000 FROM users WHERE email = @living_email;

-- 비상금은 목표 50만 원 중 38만 원을 모은 진행 상태로 둔다.
INSERT INTO emergency_funds (user_id, target_amount, current_amount, achieved_at)
SELECT id, 500000, 380000, NULL
FROM users WHERE email = @living_email;

-- 생활비는 V8처럼 상대 날짜로 생성한다.
INSERT INTO daily_living_costs (user_id, spending_date, total_amount)
WITH RECURSIVE date_range AS (
    SELECT DATE_FORMAT(@today, '%Y-%m-01') AS spending_date
    UNION ALL
    SELECT spending_date + INTERVAL 1 DAY
    FROM date_range
    WHERE spending_date < @today
)
SELECT u.id, dr.spending_date, 7000 + DAYOFWEEK(dr.spending_date) * 800
FROM users u
CROSS JOIN date_range dr
WHERE u.email = @living_email;

INSERT INTO daily_living_costs (user_id, spending_date, total_amount)
WITH RECURSIVE date_range AS (
    SELECT DATE_FORMAT(@today - INTERVAL 1 MONTH, '%Y-%m-01') AS spending_date
    UNION ALL
    SELECT spending_date + INTERVAL 1 DAY
    FROM date_range
    WHERE spending_date < LAST_DAY(@today - INTERVAL 1 MONTH)
)
-- 지난달 그래프는 요일 패턴에 날짜별 변동을 더해 자연스럽게 들쭉날쭉하게 보이도록 한다.
SELECT u.id, dr.spending_date,
       6500 + DAYOFWEEK(dr.spending_date) * 500 + MOD(DAY(dr.spending_date) * 1100, 2300)
FROM users u
CROSS JOIN date_range dr
WHERE u.email = @living_email;

-- 전날은 1단계(2만 원 이하) 성공으로 마감해, 다음 날 보상 안내 팝업을 시연한다.
UPDATE daily_living_costs dlc
INNER JOIN users u ON u.id = dlc.user_id
SET dlc.total_amount = 18000
WHERE u.email = @living_email
  AND dlc.spending_date = @today - INTERVAL 1 DAY;

-- 과거 지출은 모두 마감된 챌린지로 기록한다. 이 중 전날 기록은 오늘 00:05에 20코인이
-- 지급된 상태이므로, 서비스 첫 진입 시 프론트가 보상 안내 팝업을 표시할 수 있다.
INSERT INTO daily_challenges (daily_living_cost_id, achieved_level_id, closed_at)
SELECT
    dlc.id,
    (
        SELECT cl.id
        FROM challenge_levels cl
        WHERE cl.max_spending >= dlc.total_amount
        ORDER BY cl.max_spending ASC
        LIMIT 1
    ),
    TIMESTAMP(dlc.spending_date + INTERVAL 1 DAY) + INTERVAL 5 MINUTE
FROM daily_living_costs dlc
INNER JOIN users u ON u.id = dlc.user_id
WHERE u.email = @living_email
  AND dlc.spending_date < @today;

INSERT INTO monthly_living_costs (user_id, `year_month`, total_amount)
SELECT id, DATE_FORMAT(@today - INTERVAL 1 MONTH, '%Y-%m'), 1250000
FROM users WHERE email = @living_email
UNION ALL
SELECT id, DATE_FORMAT(@today - INTERVAL 2 MONTH, '%Y-%m'), 1380000
FROM users WHERE email = @living_email;

-- 독립 후 생활 계정은 상점 가구 구매용 500코인과 전날 일일 챌린지 1단계 보상 20코인을 보유한다.
-- 오늘의 퀴즈 정답 시 서비스가 50코인을 추가 지급한다.
INSERT INTO coin_wallets (user_id, balance)
SELECT id, 0 FROM users WHERE email = @preparing_email
UNION ALL
SELECT id, 0 FROM users WHERE email = @transition_email
UNION ALL
SELECT id, 520 FROM users WHERE email = @living_email;

-- 1번 계정은 15·30·45점 가구 선택권 3개를 미사용 상태로 둔다.
INSERT INTO furniture_reward (user_id, reward_stage)
SELECT u.id, stages.reward_stage
FROM users u
CROSS JOIN (
    SELECT 15 AS reward_stage
    UNION ALL SELECT 30
    UNION ALL SELECT 45
) stages
WHERE u.email = @preparing_email;

-- 2·3번 계정은 기본 가구 전체를 해금하고, 가구 선택권을 모두 사용한 상태다.
INSERT INTO user_furniture (user_id, furniture_id, is_placed)
SELECT u.id, f.id,
       NOT EXISTS (
           SELECT 1 FROM furniture selected
           WHERE selected.furniture_type = 'BASIC'
             AND selected.category_id = f.category_id
             AND selected.unlock_score < f.unlock_score
       )
FROM users u
CROSS JOIN furniture f
WHERE u.email IN (@transition_email, @living_email)
  AND f.furniture_type = 'BASIC'
  AND f.active = TRUE;

INSERT INTO furniture_reward (user_id, reward_stage, selected_furniture_id, claimed_at)
SELECT u.id, 15, f.id, NOW() - INTERVAL 5 DAY
FROM users u INNER JOIN furniture f ON f.name = '기본 침대'
WHERE u.email = @transition_email
UNION ALL
SELECT u.id, 30, f.id, NOW() - INTERVAL 4 DAY
FROM users u INNER JOIN furniture f ON f.name = '기본 의자'
WHERE u.email = @transition_email
UNION ALL
SELECT u.id, 45, f.id, NOW() - INTERVAL 3 DAY
FROM users u INNER JOIN furniture f ON f.name = '기본 무드등'
WHERE u.email = @transition_email
UNION ALL
SELECT u.id, 60, f.id, NOW() - INTERVAL 2 DAY
FROM users u INNER JOIN furniture f ON f.name = '기본 화분'
WHERE u.email = @transition_email
UNION ALL
SELECT u.id, 75, f.id, NOW() - INTERVAL 1 DAY
FROM users u INNER JOIN furniture f ON f.name = '기본 화분'
WHERE u.email = @transition_email;

-- 독립 후 생활 계정은 과거에 구매한 웜 오크 가구 세트를 보유·배치해
-- 방 꾸미기 완성 상태를 시연한다. 코지 코티지 세트는 미구매 상태로 남겨
-- 500코인으로 상점 구매 흐름도 이어서 시연할 수 있다.
UPDATE user_furniture uf
INNER JOIN furniture f ON f.id = uf.furniture_id
INNER JOIN users u ON u.id = uf.user_id
SET uf.is_placed = FALSE
WHERE u.email = @living_email
  AND f.furniture_type = 'BASIC';

INSERT INTO user_furniture (user_id, furniture_id, is_placed)
SELECT u.id, f.id, TRUE
FROM users u
CROSS JOIN furniture f
WHERE u.email = @living_email
  AND f.furniture_type = 'SHOP'
  AND f.name LIKE '웜 오크 %'
  AND f.active = TRUE;

-- V6이 최초로 넣은 신용관리 문항을 오늘의 문제로 고정한다.
-- 문구 비교를 피하므로 DB의 문자셋/클라이언트 인코딩과 무관하게 동작한다.
SET @credit_question_id = (
    SELECT id FROM quiz_questions ORDER BY id ASC LIMIT 1
);

INSERT INTO daily_quizzes (quiz_date, quiz_question_id)
VALUES (@today, @credit_question_id)
ON DUPLICATE KEY UPDATE quiz_question_id = VALUES(quiz_question_id);

COMMIT;

-- 프론트 VITE_DEV_USER_ID 설정에 사용할 계정 ID 목록
SELECT u.id AS user_id, u.email, up.name
FROM users u
INNER JOIN user_profiles up ON up.user_id = u.id
WHERE email IN (@preparing_email, @transition_email, @living_email)
ORDER BY FIELD(email, @preparing_email, @transition_email, @living_email);
