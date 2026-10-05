-- 로컬/DEV 전용. demo01의 예산만 변경하며 계정·가구·정책 설정은 초기화하지 않는다.
SET NAMES utf8mb4;
START TRANSACTION;
UPDATE user_profiles up
INNER JOIN users u ON u.id = up.user_id
SET up.monthly_income = 2500000,
    up.monthly_rent_limit = 800000,
    up.deposit_limit = 30000000
WHERE u.email = 'demo01@roommade.com';

UPDATE independence_progress ip
INNER JOIN users u ON u.id = ip.user_id
SET ip.current_deposit = 10000000
WHERE u.email = 'demo01@roommade.com';
COMMIT;

SELECT u.email, up.monthly_income, up.monthly_rent_limit,
       up.deposit_limit, ip.current_deposit,
       (50 - up.monthly_rent_limit * 100.0 / up.monthly_income) * 45 / 20 AS rir_score,
       ip.current_deposit * 45.0 / up.deposit_limit AS deposit_score
FROM users u
INNER JOIN user_profiles up ON up.user_id = u.id
INNER JOIN independence_progress ip ON ip.user_id = u.id
WHERE u.email = 'demo01@roommade.com';
