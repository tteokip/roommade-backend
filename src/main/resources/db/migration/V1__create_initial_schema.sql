/* =========================================================
   금융 상품 - 적금 옵션
   ========================================================= */

CREATE TABLE saving_option (

                               saving_option_id BIGINT NOT NULL AUTO_INCREMENT,
                               saving_product_id BIGINT NOT NULL,
                               interest_rate_type VARCHAR(20) NOT NULL,
                               reserve_type VARCHAR(20) NOT NULL,
                               save_term INT NOT NULL,
                               base_interest_rate DECIMAL(4,2) NULL,
                               max_interest_rate DECIMAL(4,2) NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               PRIMARY KEY (saving_option_id)
);


/* =========================================================
   금융 상품 링크
   ========================================================= */

CREATE TABLE financial_product_link (

                                        financial_product_link_id BIGINT NOT NULL AUTO_INCREMENT,
                                        financial_institution_id BIGINT NOT NULL,
                                        product_type VARCHAR(20) NOT NULL,
                                        product_code VARCHAR(50) NOT NULL,
                                        product_page_url VARCHAR(2048) NOT NULL,
                                        link_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                        verified_at DATETIME NULL,
                                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                        PRIMARY KEY (financial_product_link_id)
);


/* =========================================================
   집 비교
   ========================================================= */

CREATE TABLE `house_comparisons` (
                                     `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '집 비교 ID',
                                     `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                     `status`   VARCHAR(30)    NOT NULL   DEFAULT 'DRAFT'    COMMENT '집 비교 상태: DRAFT, AI_ANALYZED, PREFERENCE_COMPLETED, COMPLETED',
                                     `completed_at` DATETIME   NULL   COMMENT '집 비교 완료 일시',
                                     `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '집 비교 생성 일시',
                                     `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '집 비교 수정 일시',

                                     PRIMARY KEY (`id`)
);


/* =========================================================
   비상금
   ========================================================= */

CREATE TABLE `emergency_funds` (
                                   `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '비상금 관리 ID',
                                   `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                   `target_amount`    BIGINT NOT NULL   DEFAULT 0  COMMENT '비상금 목표 금액',
                                   `current_amount`   BIGINT NOT NULL   DEFAULT 0  COMMENT '현재 확보한 비상금',
                                   `achieved_at`  DATETIME   NULL   COMMENT '비상금 목표 최초 달성 일시',
                                   `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '비상금 정보 생성 일시',
                                   `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '비상금 정보 수정 일시',

                                   PRIMARY KEY (`id`)
);


/* =========================================================
   금융기관
   ========================================================= */

CREATE TABLE financial_institution (

                                       financial_institution_id BIGINT NOT NULL AUTO_INCREMENT,
                                       financial_institution_code VARCHAR(10) NOT NULL,
                                       financial_institution_name VARCHAR(100) NOT NULL,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       PRIMARY KEY (financial_institution_id)
);


/* =========================================================
   예금 상품
   ========================================================= */

CREATE TABLE deposit_product (

                                 deposit_product_id BIGINT NOT NULL AUTO_INCREMENT,
                                 financial_institution_id BIGINT NOT NULL,
                                 product_code VARCHAR(50) NOT NULL,
                                 product_name VARCHAR(100) NOT NULL,
                                 join_method TEXT NULL,
                                 join_target TEXT NULL,
                                 join_restriction CHAR(1) NULL,
                                 special_condition TEXT NULL,
                                 maturity_interest TEXT NULL,
                                 max_limit BIGINT NULL,
                                 notice TEXT NULL,
                                 disclosure_month CHAR(6) NOT NULL,
                                 disclosure_start_date DATE NULL,
                                 disclosure_end_date DATE NULL,
                                 submitted_at DATETIME NULL,
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 PRIMARY KEY (deposit_product_id)
);


/* =========================================================
   집 비교 - 밸런스 게임 질문
   ========================================================= */

CREATE TABLE `preference_questions` (
                                        `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '밸런스 게임 질문 ID',
                                        `question_order`   INT    NOT NULL   COMMENT '질문 표시 순서',
                                        `option_a_text`    VARCHAR(255)   NOT NULL   COMMENT 'A 선택지 문구',
                                        `option_a_factor`  VARCHAR(50)    NOT NULL   COMMENT 'A 선택지가 나타내는 주거 선호 요소',
                                        `option_b_text`    VARCHAR(255)   NOT NULL   COMMENT 'B 선택지 문구',
                                        `option_b_factor`  VARCHAR(50)    NOT NULL   COMMENT 'B 선택지가 나타내는 주거 선호 요소',

                                        PRIMARY KEY (`id`)
);


/* =========================================================
   금융 상품 - 예금 옵션
   ========================================================= */

CREATE TABLE deposit_option (

                                deposit_option_id BIGINT NOT NULL AUTO_INCREMENT,
                                deposit_product_id BIGINT NOT NULL,
                                interest_rate_type VARCHAR(20) NOT NULL,
                                save_term INT NOT NULL,
                                base_interest_rate DECIMAL(4,2) NULL,
                                max_interest_rate DECIMAL(4,2) NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                PRIMARY KEY (deposit_option_id)
);


/* =========================================================
   청년 정책 지역
   ========================================================= */

CREATE TABLE youth_policy_region (

                                     youth_policy_region_id BIGINT NOT NULL AUTO_INCREMENT,
                                     youth_policy_id BIGINT NOT NULL,
                                     region_code CHAR(2) NOT NULL,
                                     region_name VARCHAR(30) NOT NULL,
                                     is_nationwide BOOLEAN NOT NULL DEFAULT FALSE,
                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     PRIMARY KEY (youth_policy_region_id)
);


/* =========================================================
   사용자 프로필
   ========================================================= */

CREATE TABLE `user_profiles` (
                                 `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '사용자 프로필 ID',
                                 `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                 `name` VARCHAR(50)    NOT NULL   COMMENT '사용자 이름',
                                 `birth_date`   DATE   NOT NULL   COMMENT '생년월일',
                                 `monthly_income`   BIGINT NOT NULL   DEFAULT 0  COMMENT '월 소득',
                                 `workplace_road_address`   VARCHAR(255)   NULL   COMMENT '직장 도로명 주소',
                                 `workplace_detail_address` VARCHAR(255)   NULL   COMMENT '직장 상세 주소',
                                 `deposit_limit`    BIGINT NOT NULL   DEFAULT 0  COMMENT '회원가입 시 입력한 보증금 상한',
                                 `monthly_rent_limit`   BIGINT NOT NULL   DEFAULT 0  COMMENT '회원가입 시 입력한 월세 상한',
                                 `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '프로필 생성 일시',
                                 `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '프로필 수정 일시',

                                 PRIMARY KEY (`id`)
);


/* =========================================================
   사용자 금융 퀴즈 응답

   수정:
   quiz_id → question_id
   answered_at을 이용해 하루 1회 여부 확인
   ========================================================= */

CREATE TABLE `user_quiz_attempts` (
                                      `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '사용자 퀴즈 응답 ID',
                                      `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                      `quiz_id`  BIGINT NOT NULL   COMMENT '출제된 퀴즈 ID',
                                      `correct`  BOOLEAN    NOT NULL   COMMENT '정답 여부',
                                      `answered_at`  DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '답안 제출 일시',

                                      PRIMARY KEY (`id`)
);


/* =========================================================
   사용자 보유 가구
   ========================================================= */

CREATE TABLE `user_furniture` (
                                  `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '사용자 보유 가구 ID',
                                  `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                  `furniture_id` BIGINT NOT NULL   COMMENT '가구 ID',
                                  `is_placed`    BOOLEAN    NOT NULL   COMMENT '현재 사용자 방 배치 여부',
                                  `acquired_at`  DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '가구 획득 일시',

                                  PRIMARY KEY (`id`)
);


/* =========================================================
   가구 카테고리
   ========================================================= */

CREATE TABLE `furniture_categories` (
                                        `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '가구 카테고리 ID',
                                        `name` VARCHAR(50)    NOT NULL   COMMENT '가구 카테고리명',
                                        `position_x`   DECIMAL(10, 4) NOT NULL   DEFAULT 0  COMMENT '가구 X축 위치',
                                        `position_y`   DECIMAL(10, 4) NOT NULL   DEFAULT 0  COMMENT '가구 Y축 위치',
                                        `position_z`   DECIMAL(10, 4) NOT NULL   DEFAULT 0  COMMENT '가구 Z축 위치',

                                        PRIMARY KEY (`id`)
);


/* =========================================================
   금융 퀴즈 문제
   ========================================================= */

CREATE TABLE `quiz_questions` (
                                  `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '금융 퀴즈 문제 ID',
                                  `question` TEXT   NOT NULL   COMMENT '퀴즈 문제',
                                  `explanation`  TEXT   NOT NULL   COMMENT '정답 해설',
                                  `active`   BOOLEAN    NOT NULL   DEFAULT TRUE   COMMENT '문제 사용 여부',
                                  `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '문제 생성 일시',

                                  PRIMARY KEY (`id`)
);


/* =========================================================
   집 비교 - 밸런스 게임 응답
   ========================================================= */

CREATE TABLE `preference_answers` (
                                      `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '밸런스 게임 응답 ID',
                                      `comparison_id`    BIGINT NOT NULL   COMMENT '집 비교 ID',
                                      `question_id`  BIGINT NOT NULL   COMMENT '질문 ID',
                                      `selected_side`    VARCHAR(1) NOT NULL   COMMENT '사용자 선택: A 또는 B',
                                      `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '응답 일시',

                                      PRIMARY KEY (`id`)
);


/* =========================================================
   가구 도감
   ========================================================= */

CREATE TABLE `furniture` (
                             `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '가구 ID',
                             `category_id`  BIGINT NOT NULL   COMMENT '가구 카테고리 ID',
                             `name` VARCHAR(100)   NOT NULL   COMMENT '가구 디자인 이름',
                             `furniture_type`   VARCHAR(20)    NOT NULL   COMMENT '가구 유형: BASIC, SHOP',
                             `coin_price`   INT    NULL   COMMENT '상점 가구 구매 가격, 기본 가구는 NULL',
                             `unlock_score` INT    NULL   COMMENT '기본 가구 해금 기준 자립 준비도',
                             `asset_url`    VARCHAR(500)   NULL   COMMENT '가구 3D 에셋 또는 이미지 URL',
                             `active`   BOOLEAN    NOT NULL   DEFAULT TRUE   COMMENT '가구 사용 여부',
                             `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '가구 생성 일시',
                             `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '가구 정보 수정 일시',

                             PRIMARY KEY (`id`)
);


/* =========================================================
   일간 챌린지 결과
   ========================================================= */

CREATE TABLE `daily_challenges` (
                                    `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '일간 챌린지 ID',
                                    `daily_living_cost_id` BIGINT NOT NULL   COMMENT '챌린지 기준이 되는 일별 생활비 ID',
                                    `achieved_level_id`    BIGINT NULL   COMMENT '하루 마감 후 최종 달성한 챌린지 단계',
                                    `closed_at`    DATETIME   NULL   COMMENT '일간 챌린지 마감 일시',
                                    `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '챌린지 생성 일시',

                                    PRIMARY KEY (`id`)
);


/* =========================================================
   사용자 일별 생활비
   ========================================================= */

CREATE TABLE `daily_living_costs` (
                                      `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '일별 생활비 ID',
                                      `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                      `spending_date`    DATE   NOT NULL   COMMENT '생활비 집계 날짜',
                                      `total_amount` BIGINT NOT NULL   DEFAULT 0  COMMENT '해당 날짜의 총 생활비',
                                      `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '생활비 데이터 생성 일시',
                                      `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '생활비 데이터 수정 일시',

                                      PRIMARY KEY (`id`)
);


/* =========================================================
   사용자 월별 생활비
   ========================================================= */

CREATE TABLE `monthly_living_costs` (
                                        `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '월별 생활비 ID',
                                        `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                        `year_month`   CHAR(7)    NOT NULL   COMMENT '생활비 집계 연월: YYYY-MM',
                                        `total_amount` BIGINT NOT NULL   DEFAULT 0  COMMENT '해당 월 총 생활비',
                                        `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '월별 생활비 생성 일시',
                                        `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '월별 생활비 수정 일시',

                                        PRIMARY KEY (`id`)
);


/* =========================================================
   일간 챌린지 단계
   ========================================================= */

CREATE TABLE `challenge_levels` (
                                    `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '챌린지 단계 ID',
                                    `level`    INT    NOT NULL   COMMENT '챌린지 단계',
                                    `max_spending` BIGINT NOT NULL   COMMENT '해당 단계 달성을 위한 일일 최대 지출 금액',
                                    `reward_coin`  INT    NOT NULL   COMMENT '해당 단계 달성 시 지급 코인',

                                    PRIMARY KEY (`id`)
);


/* =========================================================
   가구 선택권 보상

   reward_stage:
   0, 15, 30, 45, 60, 75
   집 확정 시 전체 BASIC 가구 지급은
   user_furniture에 직접 추가
   ========================================================= */

CREATE TABLE `furniture_reward` (
                                    `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '가구 보상 이력 ID',
                                    `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                    `reward_stage` INT    NOT NULL   COMMENT '가구 선택권이 지급된 자립 준비도 단계: 0, 15, 30, 45, 60, 75, 100',
                                    `selected_furniture_id`    BIGINT NULL   COMMENT '해당 단계에서 사용자가 선택한 가구 ID',
                                    `granted_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '가구 선택권 지급 일시',
                                    `claimed_at`   DATETIME   NULL,

                                    PRIMARY KEY (`id`)
);


/* =========================================================
   집 비교 대상 매물
   ========================================================= */

CREATE TABLE `houses` (
                          `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '비교 매물 ID',
                          `comparison_id`    BIGINT NOT NULL   COMMENT '집 비교 ID',
                          `house_type`   VARCHAR(1) NOT NULL   COMMENT '매물 구분: A 또는 B',
                          `location` VARCHAR(255)   NULL   COMMENT '매물 위치',
                          `deposit`  BIGINT NULL   COMMENT '보증금',
                          `monthly_rent` BIGINT NULL   COMMENT '월세',
                          `maintenance_fee`  BIGINT NULL   COMMENT '관리비',
                          `area` DECIMAL(8, 2)  NULL   COMMENT '전용 면적(m²)',
                          `station_walk_minutes` INT    NULL   COMMENT '가까운 역까지 도보 시간(분)',
                          `commute_minutes`  INT    NULL   COMMENT '직장까지 예상 통근 시간(분)',
                          `floor_type`   VARCHAR(30)    NULL   COMMENT '층수 정보',
                          `room_structure`   VARCHAR(30)    NULL   COMMENT '방 구조',
                          `option_type`  VARCHAR(30)    NULL   COMMENT '옵션 정보',
                          `ai_analysis_status`   VARCHAR(30)    NULL   COMMENT 'AI 매물 정보 분석 상태',
                          `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '매물 생성 일시',
                          `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '매물 정보 수정 일시',

                          PRIMARY KEY (`id`)
);


/* =========================================================
   사용자 코인 지갑
   ========================================================= */

CREATE TABLE `coin_wallets` (
                                `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '코인 지갑 ID',
                                `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                `balance`  INT    NOT NULL   DEFAULT 0  COMMENT '현재 보유 코인',
                                `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '코인 잔액 수정 일시',

                                PRIMARY KEY (`id`)
);


/* =========================================================
   사용자 자립 준비 진행 상태
   ========================================================= */

CREATE TABLE `independence_progress` (
                                         `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '자립 준비 진행 ID',
                                         `user_id`  BIGINT NOT NULL   COMMENT '사용자 ID',
                                         `current_deposit`  BIGINT NOT NULL   DEFAULT 0  COMMENT '현재 마련한 보증금 금액',
                                         `house_compare_completed_at`   DATETIME   NULL   COMMENT '집 비교 최초 등록 완료 일시, 미완료 시 NULL',
                                         `house_confirmed_at`   DATETIME   NULL   COMMENT '집 확정 및 독립 후 전환 일시, 미확정 시 NULL',
                                         `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '자립 준비 시작 일시',
                                         `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '자립 준비 상태 수정 일시',

                                         PRIMARY KEY (`id`)
);


/* =========================================================
   청년 정책
   ========================================================= */

CREATE TABLE youth_policy (

                              youth_policy_id BIGINT NOT NULL AUTO_INCREMENT,
                              policy_no VARCHAR(30) NOT NULL,
                              policy_name VARCHAR(255) NOT NULL,
                              policy_keyword VARCHAR(255) NULL,
                              policy_description TEXT NULL,
                              policy_summary TEXT NULL,
                              support_content TEXT NULL,
                              support_amount BIGINT NULL,
                              provider_institution_code VARCHAR(20) NULL,
                              provider_institution_name VARCHAR(100) NULL,
                              zip_cd TEXT NULL,
                              application_start_date DATE NULL,
                              application_end_date DATE NULL,
                              application_period_text VARCHAR(100) NULL,
                              application_method TEXT NULL,
                              application_url VARCHAR(2048) NULL,
                              reference_url VARCHAR(2048) NULL,
                              min_age INT NULL,
                              max_age INT NULL,
                              income_condition_code VARCHAR(20) NULL,
                              min_income BIGINT NULL,
                              max_income BIGINT NULL,
                              income_condition_text TEXT NULL,
                              qualification TEXT NULL,
                              synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                              PRIMARY KEY (youth_policy_id)
);


/* =========================================================
   적금 상품
   ========================================================= */

CREATE TABLE saving_product (

                                saving_product_id BIGINT NOT NULL AUTO_INCREMENT,
                                financial_institution_id BIGINT NOT NULL,
                                product_code VARCHAR(50) NOT NULL,
                                product_name VARCHAR(100) NOT NULL,
                                join_method TEXT NULL,
                                join_target TEXT NULL,
                                join_restriction CHAR(1) NULL,
                                special_condition TEXT NULL,
                                maturity_interest TEXT NULL,
                                max_limit BIGINT NULL,
                                notice TEXT NULL,
                                disclosure_month CHAR(6) NOT NULL,
                                disclosure_start_date DATE NULL,
                                disclosure_end_date DATE NULL,
                                submitted_at DATETIME NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                PRIMARY KEY (saving_product_id)
);


/* =========================================================
   금융 퀴즈 선택지
   ========================================================= */

CREATE TABLE `quiz_choices` (
                                `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '퀴즈 선택지 ID',
                                `question_id`  BIGINT NOT NULL   COMMENT '퀴즈 문제 ID',
                                `content`  VARCHAR(500)   NOT NULL   COMMENT '선택지 내용',
                                `correct`  BOOLEAN    NOT NULL   DEFAULT FALSE  COMMENT '정답 여부',

                                PRIMARY KEY (`id`)
);


/* =========================================================
   사용자
   ========================================================= */

CREATE TABLE `users` (
                         `id`   BIGINT NOT NULL AUTO_INCREMENT   COMMENT '사용자 ID',
                         `email`    VARCHAR(255)   NOT NULL   COMMENT '로그인 이메일',
                         `password_hash`    VARCHAR(255)   NOT NULL   COMMENT '암호화된 비밀번호',
                         `created_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '회원 생성 일시',
                         `updated_at`   DATETIME   NOT NULL   DEFAULT CURRENT_TIMESTAMP  COMMENT '회원 정보 수정 일시',

                         PRIMARY KEY (`id`)
);


/* =========================================================
   UNIQUE
   ========================================================= */

/* 사용자당 프로필 하나 */
ALTER TABLE `user_profiles`
    ADD CONSTRAINT `UQ_USER_PROFILES_USER`
        UNIQUE (`user_id`);

/* 사용자당 자립 준비 상태 하나 */
ALTER TABLE `independence_progress`
    ADD CONSTRAINT `UQ_INDEPENDENCE_PROGRESS_USER`
        UNIQUE (`user_id`);

/* 사용자당 코인 지갑 하나 */
ALTER TABLE `coin_wallets`
    ADD CONSTRAINT `UQ_COIN_WALLETS_USER`
        UNIQUE (`user_id`);

/* 사용자당 비상금 목표 하나 */
ALTER TABLE `emergency_funds`
    ADD CONSTRAINT `UQ_EMERGENCY_FUNDS_USER`
        UNIQUE (`user_id`);

/* 사용자당 집 비교 하나 */
ALTER TABLE `house_comparisons`
    ADD CONSTRAINT `UQ_HOUSE_COMPARISONS_USER`
        UNIQUE (`user_id`);

/* 하나의 집 비교에서 A/B 매물 각각 하나 */
ALTER TABLE `houses`
    ADD CONSTRAINT `UQ_HOUSES_COMPARISON_TYPE`
        UNIQUE (`comparison_id`, `house_type`);

/* 한 집 비교에서 동일 질문에 한 번만 응답 */
ALTER TABLE `preference_answers`
    ADD CONSTRAINT `UQ_PREFERENCE_ANSWER`
        UNIQUE (`comparison_id`, `question_id`);

/* 사용자가 동일 가구를 중복 보유하지 않음 */
ALTER TABLE `user_furniture`
    ADD CONSTRAINT `UQ_USER_FURNITURE`
        UNIQUE (`user_id`, `furniture_id`);

/* 같은 자립 준비도 단계에서 선택권 중복 지급 방지 */
ALTER TABLE `furniture_reward`
    ADD CONSTRAINT `UQ_FURNITURE_REWARD_STAGE`
        UNIQUE (`user_id`, `reward_stage`);

/* 사용자당 하루 생활비 하나 */
ALTER TABLE `daily_living_costs`
    ADD CONSTRAINT `UQ_DAILY_LIVING_COST`
        UNIQUE (`user_id`, `spending_date`);

/* 사용자당 월 생활비 하나 */
ALTER TABLE `monthly_living_costs`
    ADD CONSTRAINT `UQ_MONTHLY_LIVING_COST`
        UNIQUE (`user_id`, `year_month`);

/* 하루 생활비 하나당 챌린지 결과 하나 */
ALTER TABLE `daily_challenges`
    ADD CONSTRAINT `UQ_DAILY_CHALLENGE`
        UNIQUE (`daily_living_cost_id`);

/* 같은 사용자가 같은 퀴즈 문제를 다시 풀지 못하도록 함 */
ALTER TABLE `user_quiz_attempts`
    ADD CONSTRAINT `UQ_USER_QUIZ_QUESTION`
        UNIQUE (`user_id`, `quiz_id`);

/* 카테고리명 중복 방지 */
ALTER TABLE `furniture_categories`
    ADD CONSTRAINT `UQ_FURNITURE_CATEGORY_NAME`
        UNIQUE (`name`);

/* 챌린지 단계 번호 중복 방지 */
ALTER TABLE `challenge_levels`
    ADD CONSTRAINT `UQ_CHALLENGE_LEVEL_NO`
        UNIQUE (`level`);

/* 사용자 이메일 중복 방지 */
ALTER TABLE `users`
    ADD CONSTRAINT `UQ_USERS_EMAIL`
        UNIQUE (`email`);

/* 금융기관 코드 중복 방지 */
ALTER TABLE `financial_institution`
    ADD CONSTRAINT `UQ_FINANCIAL_INSTITUTION_CODE`
        UNIQUE (`financial_institution_code`);

/* 금융기관별 예금 상품 코드 중복 방지 */
ALTER TABLE `deposit_product`
    ADD CONSTRAINT `UQ_DEPOSIT_PRODUCT_CODE`
        UNIQUE (`financial_institution_id`, `product_code`);

/* 예금 상품 옵션 중복 방지 */
ALTER TABLE `deposit_option`
    ADD CONSTRAINT `UQ_DEPOSIT_OPTION`
        UNIQUE (`deposit_product_id`, `interest_rate_type`, `save_term`);

/* 금융기관별 적금 상품 코드 중복 방지 */
ALTER TABLE `saving_product`
    ADD CONSTRAINT `UQ_SAVING_PRODUCT_CODE`
        UNIQUE (`financial_institution_id`, `product_code`);

/* 적금 상품 옵션 중복 방지 */
ALTER TABLE `saving_option`
    ADD CONSTRAINT `UQ_SAVING_OPTION`
        UNIQUE (`saving_product_id`, `interest_rate_type`, `reserve_type`, `save_term`);

/* 정책 번호 중복 방지 */
ALTER TABLE `youth_policy`
    ADD CONSTRAINT `UQ_YOUTH_POLICY_NO`
        UNIQUE (`policy_no`);

/* 정책별 지역 중복 방지 */
ALTER TABLE `youth_policy_region`
    ADD CONSTRAINT `UQ_YOUTH_POLICY_REGION`
        UNIQUE (`youth_policy_id`, `region_code`);

/* 금융 상품 링크 중복 방지 */
ALTER TABLE `financial_product_link`
    ADD CONSTRAINT `UQ_FINANCIAL_PRODUCT_LINK`
        UNIQUE (`financial_institution_id`, `product_type`, `product_code`);
/* =========================================================
   FOREIGN KEY
   ========================================================= */

/* ---------- 사용자 ---------- */

ALTER TABLE `user_profiles`
    ADD CONSTRAINT `FK_USER_PROFILES_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `independence_progress`
    ADD CONSTRAINT `FK_INDEPENDENCE_PROGRESS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `coin_wallets`
    ADD CONSTRAINT `FK_COIN_WALLETS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `emergency_funds`
    ADD CONSTRAINT `FK_EMERGENCY_FUNDS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);


/* ---------- 집 비교 ---------- */

ALTER TABLE `house_comparisons`
    ADD CONSTRAINT `FK_HOUSE_COMPARISONS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `houses`
    ADD CONSTRAINT `FK_HOUSES_COMPARISON`
        FOREIGN KEY (`comparison_id`)
            REFERENCES `house_comparisons` (`id`);

ALTER TABLE `preference_answers`
    ADD CONSTRAINT `FK_PREFERENCE_ANSWERS_COMPARISON`
        FOREIGN KEY (`comparison_id`)
            REFERENCES `house_comparisons` (`id`);

ALTER TABLE `preference_answers`
    ADD CONSTRAINT `FK_PREFERENCE_ANSWERS_QUESTION`
        FOREIGN KEY (`question_id`)
            REFERENCES `preference_questions` (`id`);


/* ---------- 금융 퀴즈 ---------- */

ALTER TABLE `quiz_choices`
    ADD CONSTRAINT `FK_QUIZ_CHOICES_QUESTION`
        FOREIGN KEY (`question_id`)
            REFERENCES `quiz_questions` (`id`);

ALTER TABLE `user_quiz_attempts`
    ADD CONSTRAINT `FK_USER_QUIZ_ATTEMPTS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `user_quiz_attempts`
    ADD CONSTRAINT `FK_USER_QUIZ_ATTEMPTS_QUESTION`
        FOREIGN KEY (`quiz_id`)
            REFERENCES `quiz_questions` (`id`);


/* ---------- 가구 ---------- */

ALTER TABLE `furniture`
    ADD CONSTRAINT `FK_FURNITURE_CATEGORY`
        FOREIGN KEY (`category_id`)
            REFERENCES `furniture_categories` (`id`);

ALTER TABLE `user_furniture`
    ADD CONSTRAINT `FK_USER_FURNITURE_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `user_furniture`
    ADD CONSTRAINT `FK_USER_FURNITURE_FURNITURE`
        FOREIGN KEY (`furniture_id`)
            REFERENCES `furniture` (`id`);

ALTER TABLE `furniture_reward`
    ADD CONSTRAINT `FK_FURNITURE_REWARD_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `furniture_reward`
    ADD CONSTRAINT `FK_FURNITURE_REWARD_SELECTED_FURNITURE`
        FOREIGN KEY (`selected_furniture_id`)
            REFERENCES `furniture` (`id`);


/* ---------- 독립 후 생활비 / 챌린지 ---------- */

ALTER TABLE `daily_living_costs`
    ADD CONSTRAINT `FK_DAILY_LIVING_COSTS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `monthly_living_costs`
    ADD CONSTRAINT `FK_MONTHLY_LIVING_COSTS_USER`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`);

ALTER TABLE `daily_challenges`
    ADD CONSTRAINT `FK_DAILY_CHALLENGES_LIVING_COST`
        FOREIGN KEY (`daily_living_cost_id`)
            REFERENCES `daily_living_costs` (`id`);

ALTER TABLE `daily_challenges`
    ADD CONSTRAINT `FK_DAILY_CHALLENGES_LEVEL`
        FOREIGN KEY (`achieved_level_id`)
            REFERENCES `challenge_levels` (`id`);


/* ---------- 청년 정책 ---------- */
/* ---------- 금융 상품 ---------- */


/* ---------- 청년 정책 ---------- */

ALTER TABLE `youth_policy_region`
    ADD CONSTRAINT `FK_YOUTH_POLICY_REGION_POLICY`
        FOREIGN KEY (`youth_policy_id`)
            REFERENCES `youth_policy` (`youth_policy_id`)
            ON DELETE CASCADE;


/* ---------- 금융 상품 ---------- */

ALTER TABLE `deposit_product`
    ADD CONSTRAINT `FK_DEPOSIT_PRODUCT_INSTITUTION`
        FOREIGN KEY (`financial_institution_id`)
            REFERENCES `financial_institution` (`financial_institution_id`);

ALTER TABLE `saving_product`
    ADD CONSTRAINT `FK_SAVING_PRODUCT_INSTITUTION`
        FOREIGN KEY (`financial_institution_id`)
            REFERENCES `financial_institution` (`financial_institution_id`);

ALTER TABLE `deposit_option`
    ADD CONSTRAINT `FK_DEPOSIT_OPTION_PRODUCT`
        FOREIGN KEY (`deposit_product_id`)
            REFERENCES `deposit_product` (`deposit_product_id`)
            ON DELETE CASCADE;

ALTER TABLE `saving_option`
    ADD CONSTRAINT `FK_SAVING_OPTION_PRODUCT`
        FOREIGN KEY (`saving_product_id`)
            REFERENCES `saving_product` (`saving_product_id`)
            ON DELETE CASCADE;

ALTER TABLE `financial_product_link`
    ADD CONSTRAINT `FK_FINANCIAL_PRODUCT_LINK_INSTITUTION`
        FOREIGN KEY (`financial_institution_id`)
            REFERENCES `financial_institution` (`financial_institution_id`);

/* =========================================================
   INDEX
   ========================================================= */

ALTER TABLE `youth_policy`
    ADD INDEX `IDX_YOUTH_POLICY_APPLICATION_END_DATE` (`application_end_date`);

ALTER TABLE `youth_policy_region`
    ADD INDEX `IDX_YOUTH_POLICY_REGION_REGION` (`region_code`, `is_nationwide`);
