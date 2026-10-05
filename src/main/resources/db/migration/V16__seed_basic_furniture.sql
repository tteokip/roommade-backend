INSERT IGNORE INTO furniture_categories (name) VALUES
    ('창문'),
    ('침대'),
    ('책상'),
    ('의자'),
    ('옷장'),
    ('무드등'),
    ('화분');

INSERT INTO furniture (
    category_id,
    name,
    furniture_type,
    coin_price,
    unlock_score,
    asset_url,
    active
)
SELECT id, '기본 창문', 'BASIC', NULL, 0,
       '/src/assets/room-layer/window-layer.png', TRUE
FROM furniture_categories WHERE name = '창문'
UNION ALL
SELECT id, '기본 침대', 'BASIC', NULL, 15,
       '/src/assets/room-layer/bed-layer-v4.png', TRUE
FROM furniture_categories WHERE name = '침대'
UNION ALL
SELECT id, '기본 책상', 'BASIC', NULL, 15,
       '/src/assets/room-layer/desk-layer.png', TRUE
FROM furniture_categories WHERE name = '책상'
UNION ALL
SELECT id, '기본 의자', 'BASIC', NULL, 30,
       '/src/assets/room-layer/chair-layer.png', TRUE
FROM furniture_categories WHERE name = '의자'
UNION ALL
SELECT id, '기본 옷장', 'BASIC', NULL, 30,
       '/src/assets/room-layer/closet-layer.png', TRUE
FROM furniture_categories WHERE name = '옷장'
UNION ALL
SELECT id, '기본 무드등', 'BASIC', NULL, 45,
       '/src/assets/room-layer/lamp-layer.png', TRUE
FROM furniture_categories WHERE name = '무드등'
UNION ALL
SELECT id, '기본 화분', 'BASIC', NULL, 60,
       '/src/assets/room-layer/pot-layer.png', TRUE
FROM furniture_categories WHERE name = '화분';
