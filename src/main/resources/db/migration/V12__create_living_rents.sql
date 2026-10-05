/* =========================================================
   독립 후 실제 월세
   ========================================================= */

CREATE TABLE `living_rents` (
    `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '독립 후 월세 정보 ID',
    `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
    `monthly_rent` BIGINT NOT NULL   COMMENT '독립 후 실제 확정 월세',
    `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '생성 일시',
    `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '수정 일시',

    PRIMARY KEY (`id`)
);

ALTER TABLE `living_rents`
    ADD CONSTRAINT `UQ_LIVING_RENTS_USER`
        UNIQUE (`user_id`);

ALTER TABLE `living_rents`
    ADD CONSTRAINT `FK_LIVING_RENTS_USER`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
