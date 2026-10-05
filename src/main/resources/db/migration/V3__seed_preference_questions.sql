/* =========================================================
   집 비교 - 밸런스 게임 질문 시드
   ========================================================= */

INSERT INTO `preference_questions`
(`question_order`, `option_a_text`, `option_a_factor`, `option_b_text`, `option_b_factor`)
VALUES
    (1, '월세·관리비 부담이 적은 집', 'MONTHLY_COST', '직장까지 가까운 집', 'COMMUTE'),
    (2, '역과 가까운 집', 'STATION', '더 넓은 집', 'AREA'),
    (3, '옵션이 좋은 집', 'OPTION', '월세·관리비 부담이 적은 집', 'MONTHLY_COST'),
    (4, '월세·관리비 부담이 적은 집', 'MONTHLY_COST', '보증금이 적은 집', 'DEPOSIT'),
    (5, '더 넓은 집', 'AREA', '직장까지 가까운 집', 'COMMUTE'),
    (6, '월세·관리비 부담이 적은 집', 'MONTHLY_COST', '역과 가까운 집', 'STATION'),
    (7, '보증금이 적은 집', 'DEPOSIT', '더 넓은 집', 'AREA'),
    (8, '옵션이 좋은 집', 'OPTION', '역과 가까운 집', 'STATION'),
    (9, '보증금이 적은 집', 'DEPOSIT', '직장까지 가까운 집', 'COMMUTE'),
    (10, '보증금이 적은 집', 'DEPOSIT', '옵션이 좋은 집', 'OPTION');
