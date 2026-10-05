ALTER TABLE independence_progress
    CHANGE COLUMN house_confirmed_at moved_in_at DATETIME NULL
        COMMENT '실제 독립 이후 전환 일시, 미전환 시 NULL',
    ADD COLUMN move_in_date DATE NULL
        COMMENT '입주 예정일'
        AFTER confirmed_house_id;

UPDATE independence_progress
SET move_in_date = DATE(moved_in_at)
WHERE moved_in_at IS NOT NULL
  AND move_in_date IS NULL;
