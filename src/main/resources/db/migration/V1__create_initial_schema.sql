/* =========================================================
   금융 상품 - 적금 옵션
   ========================================================= */

CREATE TABLE `saving_option` (
                                 `saving_option_id` BIGINT NOT NULL,
                                 `saving_product_id` BIGINT NOT NULL,
                                 `interest_rate_type` VARCHAR(20) NOT NULL COMMENT 's,m',
                                 `reserve_type` VARCHAR(20) NOT NULL COMMENT '정액적립식 s 자유적립식 f',
                                 `save_term` INT NOT NULL,
                                 `base_interest_rate` DECIMAL(4,2) NULL,
                                 `max_interest_rate` DECIMAL(4,2) NULL,
                                 `created_at` DATETIME NOT NULL,
                                 `updated_at` DATETIME NOT NULL
);


/* =========================================================
   금융 상품 링크
   ========================================================= */

CREATE TABLE `financial_product_link` (
                                          `financial_product_link_id` BIGINT NOT NULL,
                                          `financial_institution_id` BIGINT NOT NULL COMMENT '금융기관 ID',
                                          `product_type` VARCHAR(20) NOT NULL,
                                          `product_code` VARCHAR(50) NOT NULL,
                                          `product_page_url` VARCHAR(2048) NOT NULL,
                                          `link_status` VARCHAR(20) NOT NULL,
                                          `verified_at` DATETIME NULL,
                                          `created_at` DATETIME NOT NULL,
                                          `updated_at` DATETIME NOT NULL
);


/* =========================================================
   집 비교
   ========================================================= */

CREATE TABLE `house_comparisons` (
                                     `id` BIGINT NOT NULL COMMENT '집 비교 ID',
                                     `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                     `status` VARCHAR(30) NOT NULL DEFAULT 'DRAFT'
                                         COMMENT '집 비교 상태: DRAFT, AI_ANALYZED, PREFERENCE_COMPLETED, COMPLETED',
                                     `completed_at` DATETIME NULL COMMENT '집 비교 완료 일시',
                                     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         COMMENT '집 비교 생성 일시',
                                     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         COMMENT '집 비교 수정 일시'
);


/* =========================================================
   비상금
   ========================================================= */

CREATE TABLE `emergency_funds` (
                                   `id` BIGINT NOT NULL COMMENT '비상금 관리 ID',
                                   `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                   `target_amount` BIGINT NOT NULL DEFAULT 0 COMMENT '비상금 목표 금액',
                                   `current_amount` BIGINT NOT NULL DEFAULT 0 COMMENT '현재 확보한 비상금',
                                   `achieved_at` DATETIME NULL COMMENT '비상금 목표 최초 달성 일시',
                                   `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       COMMENT '비상금 정보 생성 일시',
                                   `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       COMMENT '비상금 정보 수정 일시'
);


/* =========================================================
   금융기관
   ========================================================= */

CREATE TABLE `financial_institution` (
                                         `financial_institution_id` BIGINT NOT NULL COMMENT '금융기관 ID',
                                         `financial_institution_code` VARCHAR(10) NOT NULL,
                                         `financial_institution_name` VARCHAR(100) NOT NULL COMMENT '금융기관명',
                                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                                         `updated_at` DATETIME NOT NULL
);


/* =========================================================
   예금 상품
   ========================================================= */

CREATE TABLE `deposit_product` (
                                   `deposit_product_id` BIGINT NOT NULL,
                                   `financial_institution_id` BIGINT NOT NULL COMMENT '금융기관 ID',
                                   `product_code` VARCHAR(50) NOT NULL COMMENT 'UNIQUE',
                                   `product_name` VARCHAR(100) NOT NULL,
                                   `join_method` TEXT NULL,
                                   `join_target` TEXT NULL,
                                   `join_restriction` CHAR(1) NULL
                                       COMMENT '1: 제한없음, 2: 서민전용, 3: 일부제한',
                                   `special_condition` TEXT NULL,
                                   `maturity_interest` TEXT NULL,
                                   `max_limit` BIGINT NULL,
                                   `notice` TEXT NULL,
                                   `disclosure_month` CHAR(6) NOT NULL,
                                   `disclosure_start_date` DATE NULL,
                                   `disclosure_end_date` DATE NULL,
                                   `submitted_at` DATETIME NULL
                                       COMMENT '금융회사가 금감원에 데이터를 제출한 날짜 정보',
                                   `created_at` DATETIME NOT NULL,
                                   `updated_at` DATETIME NOT NULL
);


/* =========================================================
   집 비교 - 밸런스 게임 질문
   ========================================================= */

CREATE TABLE `preference_questions` (
                                        `id` BIGINT NOT NULL COMMENT '밸런스 게임 질문 ID',
                                        `question_order` INT NOT NULL COMMENT '질문 표시 순서',
                                        `option_a_text` VARCHAR(255) NOT NULL COMMENT 'A 선택지 문구',
                                        `option_a_factor` VARCHAR(50) NOT NULL
                                            COMMENT 'A 선택지가 나타내는 주거 선호 요소',
                                        `option_b_text` VARCHAR(255) NOT NULL COMMENT 'B 선택지 문구',
                                        `option_b_factor` VARCHAR(50) NOT NULL
                                            COMMENT 'B 선택지가 나타내는 주거 선호 요소'
);


/* =========================================================
   금융 상품 - 예금 옵션
   ========================================================= */

CREATE TABLE `deposit_option` (
                                  `deposit_option_id` BIGINT NOT NULL,
                                  `deposit_product_id` BIGINT NOT NULL,
                                  `interest_rate_type` VARCHAR(20) NOT NULL COMMENT 's,m',
                                  `save_term` INT NOT NULL,
                                  `base_interest_rate` DECIMAL(4,2) NULL,
                                  `max_interest_rate` DECIMAL(4,2) NULL,
                                  `created_at` DATETIME NOT NULL,
                                  `updated_at` DATETIME NOT NULL
);


/* =========================================================
   청년 정책 지역
   ========================================================= */

CREATE TABLE `youth_policy_region` (
                                       `youth_policy_region_id` BIGINT NOT NULL COMMENT '청년정책 지역 연결 ID',
                                       `youth_policy_id` BIGINT NOT NULL COMMENT '내부 청년정책 ID',
                                       `region_code` CHAR(2) NOT NULL COMMENT '시도 코드. 전국은 00',
                                       `region_name` VARCHAR(30) NOT NULL COMMENT '시도명 또는 전국',
                                       `is_nationwide` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '전국 정책 여부',
                                       `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);


/* =========================================================
   사용자 프로필
   ========================================================= */

CREATE TABLE `user_profiles` (
                                 `id` BIGINT NOT NULL COMMENT '사용자 프로필 ID',
                                 `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                 `name` VARCHAR(50) NOT NULL COMMENT '사용자 이름',
                                 `birth_date` DATE NOT NULL COMMENT '생년월일',
                                 `monthly_income` BIGINT NOT NULL DEFAULT 0 COMMENT '월 소득',
                                 `workplace_road_address` VARCHAR(255) NULL COMMENT '직장 도로명 주소',
                                 `workplace_detail_address` VARCHAR(255) NULL COMMENT '직장 상세 주소',
                                 `deposit_limit` BIGINT NOT NULL DEFAULT 0
                                     COMMENT '회원가입 시 입력한 보증금 상한',
                                 `monthly_rent_limit` BIGINT NOT NULL DEFAULT 0
                                     COMMENT '회원가입 시 입력한 월세 상한',
                                 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     COMMENT '프로필 생성 일시',
                                 `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     COMMENT '프로필 수정 일시'
);


/* =========================================================
   사용자 금융 퀴즈 응답

   수정:
   quiz_id → question_id
   answered_at을 이용해 하루 1회 여부 확인
   ========================================================= */

CREATE TABLE `user_quiz_attempts` (
                                      `id` BIGINT NOT NULL COMMENT '사용자 퀴즈 응답 ID',
                                      `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                      `question_id` BIGINT NOT NULL COMMENT '출제된 금융 퀴즈 문제 ID',
                                      `correct` BOOLEAN NOT NULL COMMENT '정답 여부',
                                      `answered_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          COMMENT '답안 제출 일시'
);


/* =========================================================
   사용자 보유 가구
   ========================================================= */

CREATE TABLE `user_furniture` (
                                  `id` BIGINT NOT NULL COMMENT '사용자 보유 가구 ID',
                                  `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                  `furniture_id` BIGINT NOT NULL COMMENT '가구 ID',

                                  `is_placed` BOOLEAN NOT NULL DEFAULT FALSE
                                      COMMENT '현재 사용자 방 배치 여부',

                                  `acquired_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      COMMENT '가구 획득 일시'
);


/* =========================================================
   가구 카테고리
   ========================================================= */

CREATE TABLE `furniture_categories` (
                                        `id` BIGINT NOT NULL COMMENT '가구 카테고리 ID',
                                        `name` VARCHAR(50) NOT NULL COMMENT '가구 카테고리명',
                                        `position_x` DECIMAL(10,4) NOT NULL DEFAULT 0
                                            COMMENT '카테고리별 고정 X축 위치',
                                        `position_y` DECIMAL(10,4) NOT NULL DEFAULT 0
                                            COMMENT '카테고리별 고정 Y축 위치',
                                        `position_z` DECIMAL(10,4) NOT NULL DEFAULT 0
                                            COMMENT '카테고리별 고정 Z축 위치',
                                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            COMMENT '카테고리 생성 일시',
                                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            COMMENT '카테고리 수정 일시'
);


/* =========================================================
   금융 퀴즈 문제
   ========================================================= */

CREATE TABLE `quiz_questions` (
                                  `id` BIGINT NOT NULL COMMENT '금융 퀴즈 문제 ID',
                                  `question_text` VARCHAR(500) NOT NULL COMMENT '퀴즈 문제 내용',
                                  `explanation` TEXT NULL COMMENT '정답 해설',
                                  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      COMMENT '문제 생성 일시'
);


/* =========================================================
   집 비교 - 밸런스 게임 응답
   ========================================================= */

CREATE TABLE `preference_answers` (
                                      `id` BIGINT NOT NULL COMMENT '밸런스 게임 응답 ID',
                                      `comparison_id` BIGINT NOT NULL COMMENT '집 비교 ID',
                                      `question_id` BIGINT NOT NULL COMMENT '밸런스 게임 질문 ID',
                                      `selected_option` CHAR(1) NOT NULL COMMENT '사용자가 선택한 선택지: A 또는 B',
                                      `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          COMMENT '응답 생성 일시'
);


/* =========================================================
   가구 도감
   ========================================================= */

CREATE TABLE `furniture` (
                             `id` BIGINT NOT NULL COMMENT '가구 ID',
                             `category_id` BIGINT NOT NULL COMMENT '가구 카테고리 ID',
                             `name` VARCHAR(100) NOT NULL COMMENT '가구 디자인 이름',
                             `furniture_type` VARCHAR(20) NOT NULL
                                 COMMENT '가구 유형: BASIC, SHOP',
                             `coin_price` INT NULL
                                 COMMENT '상점 가구 구매 가격, BASIC 가구는 NULL',
                             `unlock_score` INT NULL
                                 COMMENT 'BASIC 가구 선택 가능 기준 자립 준비도, SHOP 가구는 NULL',
                             `asset_url` VARCHAR(500) NULL
                                 COMMENT '가구 3D 에셋 또는 이미지 URL',
                             `active` BOOLEAN NOT NULL DEFAULT TRUE
                                 COMMENT '가구 사용 여부',
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 COMMENT '가구 생성 일시',
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 COMMENT '가구 정보 수정 일시'
);


/* =========================================================
   일간 챌린지 결과
   ========================================================= */

CREATE TABLE `daily_challenges` (
                                    `id` BIGINT NOT NULL COMMENT '일간 챌린지 결과 ID',
                                    `daily_living_cost_id` BIGINT NOT NULL
                                        COMMENT '해당 날짜의 생활비 ID',
                                    `achieved_level_id` BIGINT NULL
                                        COMMENT '최종 달성한 챌린지 단계 ID',
                                    `reward_coin` INT NOT NULL DEFAULT 0
                                        COMMENT '최종 지급된 챌린지 보상 코인',
                                    `closed_at` DATETIME NULL
                                        COMMENT '해당 날짜 챌린지 마감 일시',
                                    `rewarded_at` DATETIME NULL
                                        COMMENT '챌린지 코인 지급 일시',
                                    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        COMMENT '챌린지 결과 생성 일시'
);


/* =========================================================
   사용자 일별 생활비
   ========================================================= */

CREATE TABLE `daily_living_costs` (
                                      `id` BIGINT NOT NULL COMMENT '일별 생활비 ID',
                                      `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                      `spending_date` DATE NOT NULL COMMENT '생활비 집계 날짜',
                                      `total_amount` BIGINT NOT NULL DEFAULT 0
                                          COMMENT '해당 날짜 총 생활비',
                                      `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          COMMENT '일별 생활비 생성 일시',
                                      `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          COMMENT '일별 생활비 수정 일시'
);


/* =========================================================
   사용자 월별 생활비
   ========================================================= */

CREATE TABLE `monthly_living_costs` (
                                        `id` BIGINT NOT NULL COMMENT '월별 생활비 ID',
                                        `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                        `year_month` CHAR(7) NOT NULL
                                            COMMENT '생활비 집계 월: YYYY-MM',
                                        `total_amount` BIGINT NOT NULL DEFAULT 0
                                            COMMENT '해당 월 총 생활비',
                                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            COMMENT '월별 생활비 생성 일시',
                                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            COMMENT '월별 생활비 수정 일시'
);


/* =========================================================
   일간 챌린지 단계
   ========================================================= */

CREATE TABLE `challenge_levels` (
                                    `id` BIGINT NOT NULL COMMENT '챌린지 단계 ID',
                                    `level_no` INT NOT NULL COMMENT '챌린지 단계: 1, 2, 3',
                                    `max_daily_amount` BIGINT NOT NULL
                                        COMMENT '해당 단계 달성을 위한 하루 최대 생활비',
                                    `reward_coin` INT NOT NULL
                                        COMMENT '해당 단계 달성 시 지급 코인',
                                    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        COMMENT '챌린지 단계 생성 일시'
);


/* =========================================================
   가구 선택권 보상

   reward_stage:
   0, 15, 30, 45, 60, 75
   집 확정 시 전체 BASIC 가구 지급은
   user_furniture에 직접 추가
   ========================================================= */

CREATE TABLE `furniture_reward` (
                                    `id` BIGINT NOT NULL COMMENT '가구 선택권 보상 ID',
                                    `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                    `reward_stage` INT NOT NULL
                                        COMMENT '가구 선택권 지급 자립 준비도 단계: 0, 15, 30, 45, 60, 75',
                                    `selected_furniture_id` BIGINT NULL
                                        COMMENT '선택권으로 선택한 가구 ID, 미사용이면 NULL',
                                    `granted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        COMMENT '가구 선택권 지급 일시',
                                    `claimed_at` DATETIME NULL
                                        COMMENT '가구 선택권 사용 일시'
);


/* =========================================================
   집 비교 대상 매물
   ========================================================= */

CREATE TABLE `houses` (
                          `id` BIGINT NOT NULL COMMENT '집 ID',
                          `comparison_id` BIGINT NOT NULL COMMENT '집 비교 ID',
                          `house_type` CHAR(1) NOT NULL
                              COMMENT '비교 대상 구분: A 또는 B',
                          `address` VARCHAR(255) NULL COMMENT '매물 주소',
                          `deposit` BIGINT NULL COMMENT '보증금',
                          `monthly_rent` BIGINT NULL COMMENT '월세',
                          `maintenance_fee` BIGINT NULL COMMENT '관리비',
                          `exclusive_area` DECIMAL(10,2) NULL COMMENT '전용 면적',
                          `floor_info` VARCHAR(50) NULL COMMENT '층수 정보',
                          `station_walk_minutes` INT NULL COMMENT '가까운 역까지 도보 시간',
                          `commute_minutes` INT NULL COMMENT '직장까지 통근 시간',
                          `description` TEXT NULL COMMENT 'AI 분석 매물 설명',
                          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                              COMMENT '매물 생성 일시',
                          `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                              COMMENT '매물 수정 일시'
);


/* =========================================================
   사용자 코인 지갑
   ========================================================= */

CREATE TABLE `coin_wallets` (
                                `id` BIGINT NOT NULL COMMENT '코인 지갑 ID',
                                `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                `balance` INT NOT NULL DEFAULT 0 COMMENT '현재 보유 코인',
                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    COMMENT '코인 지갑 생성 일시',
                                `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    COMMENT '코인 잔액 수정 일시'
);


/* =========================================================
   사용자 자립 준비 진행 상태
   ========================================================= */

CREATE TABLE `independence_progress` (
                                         `id` BIGINT NOT NULL COMMENT '자립 준비 진행 상태 ID',
                                         `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                                         `current_deposit` BIGINT NOT NULL DEFAULT 0
                                             COMMENT '현재 마련한 보증금',
                                         `house_comparison_completed_at` DATETIME NULL
                                             COMMENT '집 비교 완료 일시',
                                         `house_confirmed_at` DATETIME NULL
                                             COMMENT '실제 거주할 집 확정 일시',
                                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                             COMMENT '자립 준비 상태 생성 일시',
                                         `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                             COMMENT '자립 준비 상태 수정 일시'
);


/* =========================================================
   청년 정책
   ========================================================= */

CREATE TABLE `youth_policy` (
                                `youth_policy_id` BIGINT NOT NULL COMMENT '내부 청년정책 ID',
                                `policy_code` VARCHAR(50) NOT NULL COMMENT '정책 식별 코드',
                                `policy_name` VARCHAR(255) NOT NULL COMMENT '정책명',
                                `policy_summary` TEXT NULL COMMENT '정책 요약',
                                `support_content` TEXT NULL COMMENT '지원 내용',
                                `support_amount` BIGINT NULL COMMENT '비교용 지원 금액',
                                `application_start_date` DATE NULL COMMENT '신청 시작일',
                                `application_end_date` DATE NULL COMMENT '신청 종료일',
                                `application_url` VARCHAR(2048) NULL COMMENT '정책 신청/상세 URL',
                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);


/* =========================================================
   적금 상품
   ========================================================= */

CREATE TABLE `saving_product` (
                                  `saving_product_id` BIGINT NOT NULL,
                                  `financial_institution_id` BIGINT NOT NULL COMMENT '금융기관 ID',
                                  `product_code` VARCHAR(50) NOT NULL COMMENT 'UNIQUE',
                                  `product_name` VARCHAR(100) NOT NULL,
                                  `join_method` TEXT NULL,
                                  `join_target` TEXT NULL,
                                  `join_restriction` CHAR(1) NULL
                                      COMMENT '1: 제한없음, 2: 서민전용, 3: 일부제한',
                                  `special_condition` TEXT NULL,
                                  `maturity_interest` TEXT NULL,
                                  `max_limit` BIGINT NULL,
                                  `disclosure_month` CHAR(6) NOT NULL,
                                  `disclosure_start_date` DATE NULL,
                                  `disclosure_end_date` DATE NULL,
                                  `submitted_at` DATETIME NULL,
                                  `created_at` DATETIME NOT NULL,
                                  `updated_at` DATETIME NOT NULL
);


/* =========================================================
   금융 퀴즈 선택지
   ========================================================= */

CREATE TABLE `quiz_choices` (
                                `id` BIGINT NOT NULL COMMENT '금융 퀴즈 선택지 ID',
                                `question_id` BIGINT NOT NULL COMMENT '금융 퀴즈 문제 ID',
                                `choice_text` VARCHAR(255) NOT NULL COMMENT '선택지 내용',
                                `correct` BOOLEAN NOT NULL COMMENT '정답 여부'
);


/* =========================================================
   사용자
   ========================================================= */

CREATE TABLE `users` (
                         `id` BIGINT NOT NULL COMMENT '사용자 ID',
                         `email` VARCHAR(255) NOT NULL COMMENT '로그인 이메일',
                         `password` VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                             COMMENT '회원 생성 일시',
                         `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                             COMMENT '회원 정보 수정 일시'
);


/* =========================================================
   PRIMARY KEY
   ========================================================= */

ALTER TABLE `saving_option`
    ADD CONSTRAINT `PK_SAVING_OPTION`
        PRIMARY KEY (`saving_option_id`);

ALTER TABLE `financial_product_link`
    ADD CONSTRAINT `PK_FINANCIAL_PRODUCT_LINK`
        PRIMARY KEY (`financial_product_link_id`);

ALTER TABLE `house_comparisons`
    ADD CONSTRAINT `PK_HOUSE_COMPARISONS`
        PRIMARY KEY (`id`);

ALTER TABLE `emergency_funds`
    ADD CONSTRAINT `PK_EMERGENCY_FUNDS`
        PRIMARY KEY (`id`);

ALTER TABLE `financial_institution`
    ADD CONSTRAINT `PK_FINANCIAL_INSTITUTION`
        PRIMARY KEY (`financial_institution_id`);

ALTER TABLE `deposit_product`
    ADD CONSTRAINT `PK_DEPOSIT_PRODUCT`
        PRIMARY KEY (`deposit_product_id`);

ALTER TABLE `preference_questions`
    ADD CONSTRAINT `PK_PREFERENCE_QUESTIONS`
        PRIMARY KEY (`id`);

ALTER TABLE `deposit_option`
    ADD CONSTRAINT `PK_DEPOSIT_OPTION`
        PRIMARY KEY (`deposit_option_id`);

ALTER TABLE `youth_policy_region`
    ADD CONSTRAINT `PK_YOUTH_POLICY_REGION`
        PRIMARY KEY (`youth_policy_region_id`);

ALTER TABLE `user_profiles`
    ADD CONSTRAINT `PK_USER_PROFILES`
        PRIMARY KEY (`id`);

ALTER TABLE `user_quiz_attempts`
    ADD CONSTRAINT `PK_USER_QUIZ_ATTEMPTS`
        PRIMARY KEY (`id`);

ALTER TABLE `user_furniture`
    ADD CONSTRAINT `PK_USER_FURNITURE`
        PRIMARY KEY (`id`);

ALTER TABLE `furniture_categories`
    ADD CONSTRAINT `PK_FURNITURE_CATEGORIES`
        PRIMARY KEY (`id`);

ALTER TABLE `quiz_questions`
    ADD CONSTRAINT `PK_QUIZ_QUESTIONS`
        PRIMARY KEY (`id`);

ALTER TABLE `preference_answers`
    ADD CONSTRAINT `PK_PREFERENCE_ANSWERS`
        PRIMARY KEY (`id`);

ALTER TABLE `furniture`
    ADD CONSTRAINT `PK_FURNITURE`
        PRIMARY KEY (`id`);

ALTER TABLE `daily_challenges`
    ADD CONSTRAINT `PK_DAILY_CHALLENGES`
        PRIMARY KEY (`id`);

ALTER TABLE `daily_living_costs`
    ADD CONSTRAINT `PK_DAILY_LIVING_COSTS`
        PRIMARY KEY (`id`);

ALTER TABLE `monthly_living_costs`
    ADD CONSTRAINT `PK_MONTHLY_LIVING_COSTS`
        PRIMARY KEY (`id`);

ALTER TABLE `challenge_levels`
    ADD CONSTRAINT `PK_CHALLENGE_LEVELS`
        PRIMARY KEY (`id`);

ALTER TABLE `furniture_reward`
    ADD CONSTRAINT `PK_FURNITURE_REWARD`
        PRIMARY KEY (`id`);

ALTER TABLE `houses`
    ADD CONSTRAINT `PK_HOUSES`
        PRIMARY KEY (`id`);

ALTER TABLE `coin_wallets`
    ADD CONSTRAINT `PK_COIN_WALLETS`
        PRIMARY KEY (`id`);

ALTER TABLE `independence_progress`
    ADD CONSTRAINT `PK_INDEPENDENCE_PROGRESS`
        PRIMARY KEY (`id`);

ALTER TABLE `youth_policy`
    ADD CONSTRAINT `PK_YOUTH_POLICY`
        PRIMARY KEY (`youth_policy_id`);

ALTER TABLE `saving_product`
    ADD CONSTRAINT `PK_SAVING_PRODUCT`
        PRIMARY KEY (`saving_product_id`);

ALTER TABLE `quiz_choices`
    ADD CONSTRAINT `PK_QUIZ_CHOICES`
        PRIMARY KEY (`id`);

ALTER TABLE `users`
    ADD CONSTRAINT `PK_USERS`
        PRIMARY KEY (`id`);


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
        UNIQUE (`user_id`, `question_id`);

/* 카테고리명 중복 방지 */
ALTER TABLE `furniture_categories`
    ADD CONSTRAINT `UQ_FURNITURE_CATEGORY_NAME`
        UNIQUE (`name`);

/* 챌린지 단계 번호 중복 방지 */
ALTER TABLE `challenge_levels`
    ADD CONSTRAINT `UQ_CHALLENGE_LEVEL_NO`
        UNIQUE (`level_no`);

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

/* 금융기관별 적금 상품 코드 중복 방지 */
ALTER TABLE `saving_product`
    ADD CONSTRAINT `UQ_SAVING_PRODUCT_CODE`
        UNIQUE (`financial_institution_id`, `product_code`);

/* 정책 코드 중복 방지 */
ALTER TABLE `youth_policy`
    ADD CONSTRAINT `UQ_YOUTH_POLICY_CODE`
        UNIQUE (`policy_code`);


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
        FOREIGN KEY (`question_id`)
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

ALTER TABLE `youth_policy_region`
    ADD CONSTRAINT `FK_YOUTH_POLICY_REGION_POLICY`
        FOREIGN KEY (`youth_policy_id`)
            REFERENCES `youth_policy` (`youth_policy_id`);


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
            REFERENCES `deposit_product` (`deposit_product_id`);

ALTER TABLE `saving_option`
    ADD CONSTRAINT `FK_SAVING_OPTION_PRODUCT`
        FOREIGN KEY (`saving_product_id`)
            REFERENCES `saving_product` (`saving_product_id`);

ALTER TABLE `financial_product_link`
    ADD CONSTRAINT `FK_FINANCIAL_PRODUCT_LINK_INSTITUTION`
        FOREIGN KEY (`financial_institution_id`)
            REFERENCES `financial_institution` (`financial_institution_id`);