/* =========================================================
   금융상품 도메인 스키마 보완

   금융감독원 예금·적금 동기화 시 upsert와 생성 ID 조회가 가능하도록
   미래로 프로젝트의 금융상품 저장 구조와 정렬한다.
   ========================================================= */

/* 금융기관 */
ALTER TABLE financial_institution
    MODIFY COLUMN financial_institution_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '금융기관 ID',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

/* 예금 상품 및 옵션 */
ALTER TABLE deposit_product
    MODIFY COLUMN deposit_product_id BIGINT NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE deposit_option
    MODIFY COLUMN deposit_option_id BIGINT NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD CONSTRAINT UQ_DEPOSIT_OPTION_CONDITION
        UNIQUE (deposit_product_id, interest_rate_type, save_term);

ALTER TABLE deposit_option
    DROP FOREIGN KEY FK_DEPOSIT_OPTION_PRODUCT,
    ADD CONSTRAINT FK_DEPOSIT_OPTION_PRODUCT
        FOREIGN KEY (deposit_product_id)
            REFERENCES deposit_product (deposit_product_id)
            ON DELETE CASCADE;

/* 적금 상품 및 옵션 */
ALTER TABLE saving_product
    MODIFY COLUMN saving_product_id BIGINT NOT NULL AUTO_INCREMENT,
    ADD COLUMN notice TEXT NULL AFTER max_limit,
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE saving_option
    MODIFY COLUMN saving_option_id BIGINT NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD CONSTRAINT UQ_SAVING_OPTION_CONDITION
        UNIQUE (saving_product_id, interest_rate_type, reserve_type, save_term);

ALTER TABLE saving_option
    DROP FOREIGN KEY FK_SAVING_OPTION_PRODUCT,
    ADD CONSTRAINT FK_SAVING_OPTION_PRODUCT
        FOREIGN KEY (saving_product_id)
            REFERENCES saving_product (saving_product_id)
            ON DELETE CASCADE;

/* 금융상품 상세 페이지 링크 */
ALTER TABLE financial_product_link
    MODIFY COLUMN financial_product_link_id BIGINT NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN link_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD CONSTRAINT UQ_FINANCIAL_PRODUCT_LINK
        UNIQUE (financial_institution_id, product_type, product_code);

/* =========================================================
   청년정책 도메인 스키마 보완

   온통청년 정책 동기화와 지역·연령·소득 조건 기반 추천에 필요한
   미래로 프로젝트의 정책 저장 구조를 반영한다.
   ========================================================= */

ALTER TABLE youth_policy
    DROP INDEX UQ_YOUTH_POLICY_CODE,
    CHANGE COLUMN policy_code policy_no VARCHAR(30) NOT NULL COMMENT '온통청년 정책번호',
    MODIFY COLUMN youth_policy_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '내부 청년정책 ID',
    ADD COLUMN policy_keyword VARCHAR(255) NULL COMMENT '정책 키워드' AFTER policy_name,
    ADD COLUMN policy_description TEXT NULL COMMENT '정책 설명' AFTER policy_keyword,
    ADD COLUMN provider_institution_code VARCHAR(20) NULL COMMENT '정책 제공기관 코드' AFTER support_amount,
    ADD COLUMN provider_institution_name VARCHAR(100) NULL COMMENT '정책 제공기관명' AFTER provider_institution_code,
    ADD COLUMN zip_cd TEXT NULL COMMENT '청년정책 API 법정시군구코드 원본' AFTER provider_institution_name,
    ADD COLUMN application_period_text VARCHAR(100) NULL COMMENT '신청기간 원문' AFTER application_end_date,
    ADD COLUMN application_method TEXT NULL COMMENT '신청 방법' AFTER application_period_text,
    ADD COLUMN reference_url VARCHAR(2048) NULL COMMENT '참고 URL' AFTER application_url,
    ADD COLUMN min_age INT NULL COMMENT '지원 최소 나이' AFTER reference_url,
    ADD COLUMN max_age INT NULL COMMENT '지원 최대 나이' AFTER min_age,
    ADD COLUMN income_condition_code VARCHAR(20) NULL COMMENT '소득 조건 구분 코드' AFTER max_age,
    ADD COLUMN min_income BIGINT NULL COMMENT '최소 소득' AFTER income_condition_code,
    ADD COLUMN max_income BIGINT NULL COMMENT '최대 소득' AFTER min_income,
    ADD COLUMN income_condition_text TEXT NULL COMMENT '소득 조건 설명' AFTER max_income,
    ADD COLUMN qualification TEXT NULL COMMENT '추가 신청 자격 및 지원 대상' AFTER income_condition_text,
    ADD COLUMN synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '마지막 API 동기화 일시' AFTER qualification,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD CONSTRAINT UQ_YOUTH_POLICY_NO UNIQUE (policy_no),
    ADD INDEX IDX_YOUTH_POLICY_APPLICATION_END_DATE (application_end_date);

ALTER TABLE youth_policy_region
    MODIFY COLUMN youth_policy_region_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '청년정책 지역 연결 ID',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD CONSTRAINT UQ_YOUTH_POLICY_REGION UNIQUE (youth_policy_id, region_code),
    ADD INDEX IDX_YOUTH_POLICY_REGION_CODE (region_code, is_nationwide);

ALTER TABLE youth_policy_region
    DROP FOREIGN KEY FK_YOUTH_POLICY_REGION_POLICY,
    ADD CONSTRAINT FK_YOUTH_POLICY_REGION_POLICY
        FOREIGN KEY (youth_policy_id)
            REFERENCES youth_policy (youth_policy_id)
            ON DELETE CASCADE;
