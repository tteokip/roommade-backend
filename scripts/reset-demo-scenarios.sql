-- 발표 시연 계정만 초기화한다. V4/V8의 demo@roommade.com과 일반 사용자 데이터는 보존한다.
-- 실행: docker compose exec -T mysql sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < scripts/reset-demo-scenarios.sql

SET @preparing_email = 'demo01@roommade.com';
SET @transition_email = 'demo02@roommade.com';
SET @living_email = 'demo03@roommade.com';
SET @legacy_preparing_email = 'demo-preparing@roommade.local';
SET @legacy_transition_email = 'demo-transition@roommade.local';
SET @legacy_living_email = 'demo-living@roommade.local';

START TRANSACTION;

DELETE FROM demo_policy_feature WHERE email IN (@preparing_email, @transition_email, @living_email);

DELETE dc FROM daily_challenges dc INNER JOIN daily_living_costs dlc ON dlc.id = dc.daily_living_cost_id INNER JOIN users u ON u.id = dlc.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE uqa FROM user_quiz_attempts uqa INNER JOIN users u ON u.id = uqa.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE pa FROM preference_answers pa INNER JOIN house_comparisons hc ON hc.id = pa.comparison_id INNER JOIN users u ON u.id = hc.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE lr FROM living_rents lr INNER JOIN users u ON u.id = lr.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE ef FROM emergency_funds ef INNER JOIN users u ON u.id = ef.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE fr FROM furniture_reward fr INNER JOIN users u ON u.id = fr.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE uf FROM user_furniture uf INNER JOIN users u ON u.id = uf.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE cw FROM coin_wallets cw INNER JOIN users u ON u.id = cw.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE dlc FROM daily_living_costs dlc INNER JOIN users u ON u.id = dlc.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE mlc FROM monthly_living_costs mlc INNER JOIN users u ON u.id = mlc.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE ip FROM independence_progress ip INNER JOIN users u ON u.id = ip.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE h FROM houses h INNER JOIN house_comparisons hc ON hc.id = h.comparison_id INNER JOIN users u ON u.id = hc.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE hc FROM house_comparisons hc INNER JOIN users u ON u.id = hc.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE up FROM user_profiles up INNER JOIN users u ON u.id = up.user_id WHERE u.email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);
DELETE FROM users WHERE email IN (@preparing_email, @transition_email, @living_email, @legacy_preparing_email, @legacy_transition_email, @legacy_living_email);

COMMIT;
