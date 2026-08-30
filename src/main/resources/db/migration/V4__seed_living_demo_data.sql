/* =========================================================
   living 도메인 시연용 데모 데이터
   ========================================================= */

INSERT INTO users (email, password_hash)
VALUES ('demo@roommade.com', 'demo-hash-placeholder');

INSERT INTO emergency_funds (user_id, target_amount, current_amount, achieved_at)
SELECT id, 1000000, 1200000, CURRENT_TIMESTAMP
FROM users
WHERE email = 'demo@roommade.com';
