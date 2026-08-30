ALTER TABLE independence_progress
    ADD COLUMN confirmed_house_id BIGINT NULL
        COMMENT '확정한 비교 매물 ID, 다른 집 확정 시 NULL'
        AFTER house_compare_completed_at,
    ADD CONSTRAINT FK_INDEPENDENCE_PROGRESS_CONFIRMED_HOUSE
        FOREIGN KEY (confirmed_house_id)
        REFERENCES houses (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;
