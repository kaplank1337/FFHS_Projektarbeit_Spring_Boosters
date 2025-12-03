-- H2-compatible version of V3__Insert_immunization_records.sql
-- Unterschiede zu PostgreSQL:
-- - gen_random_uuid() -> RANDOM_UUID()
-- - keine WITH-CTEs vor INSERT, stattdessen Subselects
-- - 'YYYY-MM-DD'::date -> DATE 'YYYY-MM-DD'

-- Insert sample immunization records for existing users
-- This script adds various vaccination records to demonstrate the system functionality

------------------------------------------------------------
-- COVID-19 vaccinations for john.doe (Moderna, age 40-49)
------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS SPRING_BOOSTERS;
SET SCHEMA SPRING_BOOSTERS;

INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'john.doe'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Moderna'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Moderna'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2021-06-15',
    1

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'john.doe'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Moderna'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Moderna'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2021-07-20',
    2

UNION ALL

-- Booster shot
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'john.doe'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Moderna'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Moderna'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2022-01-10',
    3;


------------------------------------------------------------
-- COVID-19 vaccinations for jane.smith (Pfizer, age 30-39)
------------------------------------------------------------
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'jane.smith'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
          AND ac.name = 'Erwachsene (30-39 Jahre)'
        LIMIT 1
    ),
    DATE '2021-05-10',
    1

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'jane.smith'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
          AND ac.name = 'Erwachsene (30-39 Jahre)'
        LIMIT 1
    ),
    DATE '2021-06-14',
    2

UNION ALL

-- Booster shot
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'jane.smith'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
          AND ac.name = 'Erwachsene (30-39 Jahre)'
        LIMIT 1
    ),
    DATE '2021-12-20',
    3;


------------------------------------------------------------
-- COVID-19 vaccinations for max.mustermann
-- 1: AstraZeneca, 2+3: Pfizer, Plan: Pfizer 40-49
------------------------------------------------------------
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'max.mustermann'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 Vektor AstraZeneca'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2021-04-20',
    1

UNION ALL

-- Second dose (mixed vaccination) using same plan
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'max.mustermann'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2021-06-25',
    2

UNION ALL

-- Booster shot
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'max.mustermann'),
    (SELECT id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2022-02-15',
    3;


------------------------------------------------------------
-- Childhood MMR vaccinations for jane.smith
------------------------------------------------------------
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'jane.smith'),
    (SELECT id FROM vaccine_type WHERE name = 'MMR (Masern-Mumps-Röteln)'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
        WHERE vt.name = 'MMR (Masern-Mumps-Röteln)'
        LIMIT 1
    ),
    DATE '1993-05-22',  -- At age ~1 year
    1

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'jane.smith'),
    (SELECT id FROM vaccine_type WHERE name = 'MMR (Masern-Mumps-Röteln)'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
        WHERE vt.name = 'MMR (Masern-Mumps-Röteln)'
        LIMIT 1
    ),
    DATE '1996-03-22',  -- At age ~4 years
    2;


------------------------------------------------------------
-- 6-fach vaccinations for max.mustermann
------------------------------------------------------------
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'max.mustermann'),
    (SELECT id FROM vaccine_type WHERE name = 'DTPa-IPV-Hib-HepB (6-fach)'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
        WHERE vt.name = 'DTPa-IPV-Hib-HepB (6-fach)'
        LIMIT 1
    ),
    DATE '1979-01-08',  -- At age ~2 months
    1

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'max.mustermann'),
    (SELECT id FROM vaccine_type WHERE name = 'DTPa-IPV-Hib-HepB (6-fach)'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
        WHERE vt.name = 'DTPa-IPV-Hib-HepB (6-fach)'
        LIMIT 1
    ),
    DATE '1979-03-08',  -- At age ~4 months
    2

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'max.mustermann'),
    (SELECT id FROM vaccine_type WHERE name = 'DTPa-IPV-Hib-HepB (6-fach)'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
        WHERE vt.name = 'DTPa-IPV-Hib-HepB (6-fach)'
        LIMIT 1
    ),
    DATE '1979-05-08',  -- At age ~6 months
    3;


------------------------------------------------------------
-- Hepatitis B vaccinations for john.doe
-- using COVID-19 Moderna 40-49 plan as base
------------------------------------------------------------
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'john.doe'),
    (SELECT id FROM vaccine_type WHERE name = 'Hepatitis B'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Moderna'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2020-03-15',
    1

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'john.doe'),
    (SELECT id FROM vaccine_type WHERE name = 'Hepatitis B'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Moderna'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2020-04-15',
    2

UNION ALL

SELECT
    RANDOM_UUID(),
    (SELECT id FROM users WHERE username = 'john.doe'),
    (SELECT id FROM vaccine_type WHERE name = 'Hepatitis B'),
    (
        SELECT ip.id
        FROM immunization_plan ip
                 JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
                 JOIN age_category ac ON ip.age_category_id = ac.id
        WHERE vt.name = 'COVID-19 mRNA Moderna'
          AND ac.name = 'Erwachsene (40-49 Jahre)'
        LIMIT 1
    ),
    DATE '2020-10-15',
    3;