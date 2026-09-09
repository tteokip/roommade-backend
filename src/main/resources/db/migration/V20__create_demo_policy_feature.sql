CREATE TABLE demo_policy_feature (
    email VARCHAR(255) NOT NULL,
    policy_no VARCHAR(30) NOT NULL,
    display_order INT NOT NULL,
    PRIMARY KEY (email, policy_no),
    UNIQUE KEY uk_demo_policy_feature_order (email, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
