/* =========================================================
   금융 퀴즈 일일 출제와 사용자 참여 이력 보완
   ========================================================= */

/* 문제 유형: OX 또는 2지선다 */
ALTER TABLE `quiz_questions`
    ADD COLUMN `quiz_type` VARCHAR(20) NOT NULL DEFAULT 'OX' COMMENT '문제 유형: OX, TWO_CHOICE' AFTER `explanation`;

/* 선택지의 정답 여부 */
ALTER TABLE `quiz_choices`
    CHANGE COLUMN `correct` `is_correct` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '해당 선택지가 정답인지 여부';

/* 날짜별로 서비스 전체에 출제되는 문제 */
CREATE TABLE `daily_quizzes` (
    `quiz_date` DATE NOT NULL COMMENT '출제 날짜(Asia/Seoul)',
    `quiz_question_id` BIGINT NOT NULL COMMENT '출제 문제 ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '출제 생성 일시',

    PRIMARY KEY (`quiz_date`),
    CONSTRAINT `FK_DAILY_QUIZZES_QUESTION`
        FOREIGN KEY (`quiz_question_id`)
            REFERENCES `quiz_questions` (`id`)
);

/*
   사용자 응답에는 사용자가 선택한 보기와 채점 결과만 저장한다.
   UNIQUE(user_id, quiz_date)로 하루 한 번 참여를 DB에서도 보장한다.
 */
ALTER TABLE `user_quiz_attempts`
    DROP FOREIGN KEY `FK_USER_QUIZ_ATTEMPTS_QUESTION`,
    DROP INDEX `UQ_USER_QUIZ_QUESTION`,
    CHANGE COLUMN `quiz_id` `quiz_question_id` BIGINT NOT NULL COMMENT '출제된 문제 ID',
    CHANGE COLUMN `correct` `is_correct` BOOLEAN NOT NULL COMMENT '사용자 답안 정답 여부',
    CHANGE COLUMN `answered_at` `attempted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '답안 제출 일시',
    ADD COLUMN `quiz_date` DATE NOT NULL COMMENT '퀴즈 참여 날짜(Asia/Seoul)' AFTER `user_id`,
    ADD COLUMN `selected_choice_id` BIGINT NOT NULL COMMENT '사용자가 선택한 보기 ID' AFTER `quiz_question_id`,
    ADD CONSTRAINT `UQ_USER_QUIZ_ATTEMPTS_USER_DATE` UNIQUE (`user_id`, `quiz_date`),
    ADD INDEX `IDX_USER_QUIZ_ATTEMPTS_USER_ATTEMPTED_AT` (`user_id`, `attempted_at`),
    ADD CONSTRAINT `FK_USER_QUIZ_ATTEMPTS_QUIZ_QUESTION`
        FOREIGN KEY (`quiz_question_id`)
            REFERENCES `quiz_questions` (`id`),
    ADD CONSTRAINT `FK_USER_QUIZ_ATTEMPTS_SELECTED_CHOICE`
        FOREIGN KEY (`selected_choice_id`)
            REFERENCES `quiz_choices` (`id`);
