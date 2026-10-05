/* =========================================================
   상점 가구 가격 설정 (카테고리별 동일 가격 적용)
   ========================================================= */

UPDATE furniture f
INNER JOIN furniture_categories fc ON fc.id = f.category_id
SET f.coin_price = CASE fc.name
    WHEN '창문' THEN 250
    WHEN '침대' THEN 400
    WHEN '책상' THEN 300
    WHEN '의자' THEN 250
    WHEN '옷장' THEN 350
    WHEN '무드등' THEN 200
    WHEN '화분' THEN 150
END
WHERE f.furniture_type = 'SHOP';
