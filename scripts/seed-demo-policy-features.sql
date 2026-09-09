-- V20 마이그레이션 적용 후 로컬/DEV에서 실행한다. 계정 초기화 없이 시연 정책만 설정한다.
SET NAMES utf8mb4;
START TRANSACTION;
DELETE FROM demo_policy_feature WHERE email IN ('demo01@roommade.com', 'demo02@roommade.com', 'demo03@roommade.com');
INSERT INTO demo_policy_feature (email, policy_no, display_order) VALUES
    ('demo01@roommade.com', '20260616005400113238', 1),
    ('demo01@roommade.com', '20250316005400210632', 2),
    ('demo01@roommade.com', '20250316005400210626', 3),
    ('demo02@roommade.com', '20260616005400113238', 1),
    ('demo02@roommade.com', '20250316005400210632', 2),
    ('demo02@roommade.com', '20250316005400210626', 3),
    ('demo03@roommade.com', '20250617005400110952', 1),
    ('demo03@roommade.com', '20250903005400211608', 2);
COMMIT;

-- 정책명이 NULL이면 정책 동기화 상태를 확인한다.
SELECT dpf.email, dpf.display_order, dpf.policy_no, yp.policy_name
FROM demo_policy_feature dpf
LEFT JOIN youth_policy yp ON yp.policy_no = dpf.policy_no
ORDER BY dpf.email, dpf.display_order;
