-- H2-compatible version of V1__Create_immunization_schema.sql

-- Create users table
CREATE TABLE IF NOT EXISTS users (
                                     id UUID DEFAULT RANDOM_UUID() NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    );

-- Create vaccine_type table
CREATE TABLE IF NOT EXISTS vaccine_type (
                                            id UUID DEFAULT RANDOM_UUID() NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    );

-- Create active_substance table
CREATE TABLE IF NOT EXISTS active_substance (
                                                id UUID DEFAULT RANDOM_UUID() NOT NULL,
    name VARCHAR(255) NOT NULL,
    synonyms TEXT ARRAY, -- stored as text array for H2 to match TEXT[] semantics
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    );

-- Create age_category table
CREATE TABLE IF NOT EXISTS age_category (
                                            id UUID DEFAULT RANDOM_UUID() NOT NULL,
    name VARCHAR(255) NOT NULL,
    age_min_days INT NOT NULL,
    age_max_days INT, -- NULL means open-ended
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    );

-- Create vaccine_type_active_substance junction table
CREATE TABLE IF NOT EXISTS vaccine_type_active_substance (
                                                             vaccine_type_id UUID NOT NULL,
                                                             active_substance_id UUID NOT NULL,
                                                             qualitative_amount VARCHAR(255),
    PRIMARY KEY (vaccine_type_id, active_substance_id),
    FOREIGN KEY (vaccine_type_id) REFERENCES vaccine_type(id) ON DELETE CASCADE,
    FOREIGN KEY (active_substance_id) REFERENCES active_substance(id) ON DELETE CASCADE
    );

-- Create immunization_plan table
CREATE TABLE IF NOT EXISTS immunization_plan (
                                                 id UUID DEFAULT RANDOM_UUID() NOT NULL,
    name VARCHAR(255) NOT NULL,
    vaccine_type_id UUID NOT NULL,
    age_category_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (vaccine_type_id) REFERENCES vaccine_type(id) ON DELETE RESTRICT,
    FOREIGN KEY (age_category_id) REFERENCES age_category(id) ON DELETE RESTRICT
    );

-- Create immunization_plan_series table
CREATE TABLE IF NOT EXISTS immunization_plan_series (
                                                        id UUID DEFAULT RANDOM_UUID() NOT NULL,
    immunization_plan_id UUID NOT NULL,
    series_name VARCHAR(255) NOT NULL,
    required_doses INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (immunization_plan_id) REFERENCES immunization_plan(id) ON DELETE CASCADE
    );

-- Create follow_up_rule table
CREATE TABLE IF NOT EXISTS follow_up_rule (
                                              id UUID DEFAULT RANDOM_UUID() NOT NULL,
    from_plan_id UUID,
    to_plan_id UUID,
    required_series_id UUID,
    min_completed_doses INT NOT NULL,
    target_min_age_days INT,
    target_max_age_days INT,
    min_interval_days_since_last INT,
    preferred_age_days INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (from_plan_id) REFERENCES immunization_plan(id) ON DELETE CASCADE,
    FOREIGN KEY (to_plan_id) REFERENCES immunization_plan(id) ON DELETE CASCADE,
    FOREIGN KEY (required_series_id) REFERENCES immunization_plan_series(id) ON DELETE CASCADE
    );

-- Create immunization_record table
CREATE TABLE IF NOT EXISTS immunization_record (
                                                   id UUID DEFAULT RANDOM_UUID() NOT NULL,
    user_id UUID NOT NULL,
    vaccine_type_id UUID NOT NULL,
    immunization_plan_id UUID NOT NULL,
    administered_on DATE NOT NULL,
    dose_order_claimed INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vaccine_type_id) REFERENCES vaccine_type(id) ON DELETE RESTRICT,
    FOREIGN KEY (immunization_plan_id) REFERENCES immunization_plan(id) ON DELETE RESTRICT
    );

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_vaccine_type_name ON vaccine_type(name);
CREATE INDEX IF NOT EXISTS idx_active_substance_name ON active_substance(name);
CREATE INDEX IF NOT EXISTS idx_age_category_age_range ON age_category(age_min_days, age_max_days);
CREATE INDEX IF NOT EXISTS idx_immunization_plan_vaccine_type ON immunization_plan(vaccine_type_id);
CREATE INDEX IF NOT EXISTS idx_immunization_plan_age_category ON immunization_plan(age_category_id);
CREATE INDEX IF NOT EXISTS idx_immunization_record_user ON immunization_record(user_id);
CREATE INDEX IF NOT EXISTS idx_immunization_record_vaccine_type ON immunization_record(vaccine_type_id);
CREATE INDEX IF NOT EXISTS idx_immunization_record_plan ON immunization_record(immunization_plan_id);
CREATE INDEX IF NOT EXISTS idx_immunization_record_administered_on ON immunization_record(administered_on);