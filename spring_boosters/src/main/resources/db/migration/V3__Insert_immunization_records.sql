-- Insert sample immunization records for existing users
-- This script adds various vaccination records to demonstrate the system functionality

-- Insert COVID-19 vaccination records for john.doe (born 1985-05-15, age ~40)
WITH user_john AS (
    SELECT id as user_id FROM users WHERE username = 'john.doe'
),
covid_moderna AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'COVID-19 mRNA Moderna'
),
covid_plan_john AS (
    SELECT ip.id as plan_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    JOIN age_category ac ON ip.age_category_id = ac.id
    WHERE vt.name = 'COVID-19 mRNA Moderna'
    AND ac.name = 'Erwachsene (40-49 Jahre)'
    LIMIT 1
)
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    gen_random_uuid(),
    uj.user_id,
    cm.vaccine_id,
    cp.plan_id,
    '2021-06-15'::date,
    1
FROM user_john uj, covid_moderna cm, covid_plan_john cp

UNION ALL

SELECT
    gen_random_uuid(),
    uj.user_id,
    cm.vaccine_id,
    cp.plan_id,
    '2021-07-20'::date,
    2
FROM user_john uj, covid_moderna cm, covid_plan_john cp

UNION ALL

-- Booster shot
SELECT
    gen_random_uuid(),
    uj.user_id,
    cm.vaccine_id,
    cp.plan_id,
    '2022-01-10'::date,
    3
FROM user_john uj, covid_moderna cm, covid_plan_john cp;

-- Insert COVID-19 vaccination records for jane.smith (born 1992-03-22, age ~33)
WITH user_jane AS (
    SELECT id as user_id FROM users WHERE username = 'jane.smith'
),
covid_pfizer AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'
),
covid_plan_jane AS (
    SELECT ip.id as plan_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    JOIN age_category ac ON ip.age_category_id = ac.id
    WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'
    AND ac.name = 'Erwachsene (30-39 Jahre)'
    LIMIT 1
)
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    gen_random_uuid(),
    uj.user_id,
    cp.vaccine_id,
    cpl.plan_id,
    '2021-05-10'::date,
    1
FROM user_jane uj, covid_pfizer cp, covid_plan_jane cpl

UNION ALL

SELECT
    gen_random_uuid(),
    uj.user_id,
    cp.vaccine_id,
    cpl.plan_id,
    '2021-06-14'::date,
    2
FROM user_jane uj, covid_pfizer cp, covid_plan_jane cpl

UNION ALL

-- Booster shot
SELECT
    gen_random_uuid(),
    uj.user_id,
    cp.vaccine_id,
    cpl.plan_id,
    '2021-12-20'::date,
    3
FROM user_jane uj, covid_pfizer cp, covid_plan_jane cpl;

-- Insert COVID-19 vaccination records for max.mustermann (born 1978-11-08, age ~47)
-- First two doses with AstraZeneca plan, then use Pfizer plan for mixed vaccination
WITH user_max AS (
    SELECT id as user_id FROM users WHERE username = 'max.mustermann'
),
covid_astrazeneca AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'COVID-19 Vektor AstraZeneca'
),
covid_pfizer_max AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'COVID-19 mRNA Pfizer-BioNTech'
),
-- For AstraZeneca we need any available COVID plan for 40-49 age group
covid_plan_max AS (
    SELECT ip.id as plan_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    JOIN age_category ac ON ip.age_category_id = ac.id
    WHERE vt.name = 'COVID-19 mRNA Pfizer-BioNTech'  -- Use Pfizer plan as base
    AND ac.name = 'Erwachsene (40-49 Jahre)'
    LIMIT 1
)
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    gen_random_uuid(),
    um.user_id,
    ca.vaccine_id,
    cpm.plan_id,
    '2021-04-20'::date,
    1
FROM user_max um, covid_astrazeneca ca, covid_plan_max cpm

UNION ALL

-- Second dose with different vaccine (mixed vaccination) using same plan
SELECT
    gen_random_uuid(),
    um.user_id,
    cpf.vaccine_id,
    cpm.plan_id,
    '2021-06-25'::date,
    2
FROM user_max um, covid_pfizer_max cpf, covid_plan_max cpm

UNION ALL

-- Booster shot
SELECT
    gen_random_uuid(),
    um.user_id,
    cpf.vaccine_id,
    cpm.plan_id,
    '2022-02-15'::date,
    3
FROM user_max um, covid_pfizer_max cpf, covid_plan_max cpm;

-- Insert some childhood vaccination records for jane.smith
-- Using MMR plan for childhood vaccinations
WITH user_jane AS (
    SELECT id as user_id FROM users WHERE username = 'jane.smith'
),
mmr_vaccine AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'MMR (Masern-Mumps-Röteln)'
),
mmr_plan AS (
    SELECT ip.id as plan_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    WHERE vt.name = 'MMR (Masern-Mumps-Röteln)'
    LIMIT 1
)
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    gen_random_uuid(),
    uj.user_id,
    mv.vaccine_id,
    mp.plan_id,
    '1993-05-22'::date,  -- At age ~1 year
    1
FROM user_jane uj, mmr_vaccine mv, mmr_plan mp

UNION ALL

SELECT
    gen_random_uuid(),
    uj.user_id,
    mv.vaccine_id,
    mp.plan_id,
    '1996-03-22'::date,  -- At age ~4 years
    2
FROM user_jane uj, mmr_vaccine mv, mmr_plan mp;

-- Insert some 6-fach vaccination records for max.mustermann
WITH user_max AS (
    SELECT id as user_id FROM users WHERE username = 'max.mustermann'
),
sixfold_vaccine AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'DTPa-IPV-Hib-HepB (6-fach)'
),
sixfold_plan AS (
    SELECT ip.id as plan_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    WHERE vt.name = 'DTPa-IPV-Hib-HepB (6-fach)'
    LIMIT 1
)
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    gen_random_uuid(),
    um.user_id,
    sv.vaccine_id,
    sp.plan_id,
    '1979-01-08'::date,  -- At age ~2 months
    1
FROM user_max um, sixfold_vaccine sv, sixfold_plan sp

UNION ALL

SELECT
    gen_random_uuid(),
    um.user_id,
    sv.vaccine_id,
    sp.plan_id,
    '1979-03-08'::date,  -- At age ~4 months
    2
FROM user_max um, sixfold_vaccine sv, sixfold_plan sp

UNION ALL

SELECT
    gen_random_uuid(),
    um.user_id,
    sv.vaccine_id,
    sp.plan_id,
    '1979-05-08'::date,  -- At age ~6 months
    3
FROM user_max um, sixfold_vaccine sv, sixfold_plan sp;

-- Insert Hepatitis B vaccination for john.doe using a COVID plan as base (since Hepatitis B plan may not exist)
WITH user_john AS (
    SELECT id as user_id FROM users WHERE username = 'john.doe'
),
hepb_vaccine AS (
    SELECT id as vaccine_id FROM vaccine_type WHERE name = 'Hepatitis B'
),
base_plan AS (
    SELECT ip.id as plan_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    JOIN age_category ac ON ip.age_category_id = ac.id
    WHERE vt.name = 'COVID-19 mRNA Moderna'
    AND ac.name = 'Erwachsene (40-49 Jahre)'
    LIMIT 1
)
INSERT INTO immunization_record (id, user_id, vaccine_type_id, immunization_plan_id, administered_on, dose_order_claimed)
SELECT
    gen_random_uuid(),
    uj.user_id,
    hv.vaccine_id,
    bp.plan_id,  -- Use COVID plan as reference instead of NULL
    '2020-03-15'::date,
    1
FROM user_john uj, hepb_vaccine hv, base_plan bp

UNION ALL

SELECT
    gen_random_uuid(),
    uj.user_id,
    hv.vaccine_id,
    bp.plan_id,
    '2020-04-15'::date,
    2
FROM user_john uj, hepb_vaccine hv, base_plan bp

UNION ALL

SELECT
    gen_random_uuid(),
    uj.user_id,
    hv.vaccine_id,
    bp.plan_id,
    '2020-10-15'::date,
    3
FROM user_john uj, hepb_vaccine hv, base_plan bp;
