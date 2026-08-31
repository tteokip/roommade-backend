/* =========================================================
   매물 통근 시간을 단일값 대신 범위(최소~최대)로 저장
   ========================================================= */

/* TMAP 지오코딩 후보가 여러 개일 수 있어 통근 시간을 범위로 계산한다. */
ALTER TABLE `houses`
    ADD COLUMN `commute_min_minutes` INT NULL COMMENT '직장까지 예상 통근 시간(분) 최솟값' AFTER `commute_minutes`,
    ADD COLUMN `commute_max_minutes` INT NULL COMMENT '직장까지 예상 통근 시간(분) 최댓값' AFTER `commute_min_minutes`;

/* 기존 단일값을 최소·최대에 동일하게 이전한다. */
UPDATE `houses`
SET `commute_min_minutes` = `commute_minutes`,
    `commute_max_minutes` = `commute_minutes`
WHERE `commute_minutes` IS NOT NULL;

/* 이전이 끝난 단일 컬럼은 제거한다. */
ALTER TABLE `houses`
    DROP COLUMN `commute_minutes`;
