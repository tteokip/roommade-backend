/* =========================================================
   상점 판매용 가구 디자인
   가격은 상품 정책 확정 후 별도 마이그레이션에서 설정한다.
   ========================================================= */

INSERT INTO furniture (
    category_id,
    name,
    furniture_type,
    coin_price,
    unlock_score,
    asset_url,
    active
)
SELECT
    fc.id,
    design.name,
    'SHOP',
    NULL,
    NULL,
    design.asset_url,
    TRUE
FROM (
    SELECT '창문' AS category_name, '웜 오크 창문' AS name,
           '/src/assets/room-layer/warm-oak/window-layer.png' AS asset_url
    UNION ALL
    SELECT '침대', '웜 오크 침대',
           '/src/assets/room-layer/warm-oak/bed-layer.png'
    UNION ALL
    SELECT '책상', '웜 오크 책상',
           '/src/assets/room-layer/warm-oak/desk-layer.png'
    UNION ALL
    SELECT '의자', '웜 오크 의자',
           '/src/assets/room-layer/warm-oak/chair-layer.png'
    UNION ALL
    SELECT '옷장', '웜 오크 옷장',
           '/src/assets/room-layer/warm-oak/closet-layer.png'
    UNION ALL
    SELECT '무드등', '웜 오크 무드등',
           '/src/assets/room-layer/warm-oak/lamp-layer.png'
    UNION ALL
    SELECT '화분', '웜 오크 화분',
           '/src/assets/room-layer/warm-oak/pot-layer.png'
    UNION ALL
    SELECT '창문', '코지 코티지 창문',
           '/src/assets/room-layer/cozy-cottage/window-layer.png'
    UNION ALL
    SELECT '침대', '코지 코티지 침대',
           '/src/assets/room-layer/cozy-cottage/bed-layer.png'
    UNION ALL
    SELECT '책상', '코지 코티지 책상',
           '/src/assets/room-layer/cozy-cottage/desk-layer.png'
    UNION ALL
    SELECT '의자', '코지 코티지 의자',
           '/src/assets/room-layer/cozy-cottage/chair-layer.png'
    UNION ALL
    SELECT '옷장', '코지 코티지 옷장',
           '/src/assets/room-layer/cozy-cottage/closet-layer.png'
    UNION ALL
    SELECT '무드등', '코지 코티지 무드등',
           '/src/assets/room-layer/cozy-cottage/lamp-layer.png'
    UNION ALL
    SELECT '화분', '코지 코티지 화분',
           '/src/assets/room-layer/cozy-cottage/pot-layer.png'
) design
INNER JOIN furniture_categories fc ON fc.name = design.category_name;
