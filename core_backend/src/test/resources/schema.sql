-- H2-compatible schema for tests (Postgres MODE compatibility)
CREATE TABLE active_substance (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  synonyms VARCHAR(1024),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE users (
  id UUID PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  birth_date DATE NOT NULL,
  role VARCHAR(50),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE vaccine_type (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(100),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE age_category (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  age_min_days INTEGER,
  age_max_days INTEGER,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE immunization_plan (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  vaccine_type_id UUID,
  age_category_id UUID,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE immunization_record (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    administered_on DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    dose_order_claimed INT,
    immunization_plan_id VARCHAR(36),
    user_id VARCHAR(36),
    vaccine_type_id VARCHAR(36)
    );

