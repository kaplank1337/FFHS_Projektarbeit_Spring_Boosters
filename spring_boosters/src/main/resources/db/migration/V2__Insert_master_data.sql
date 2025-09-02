-- Insert default active substances
INSERT INTO active_substance (id, name, synonyms) VALUES
    (gen_random_uuid(), 'mRNA-1273', ARRAY['Moderna mRNA', 'Spikevax']),
    (gen_random_uuid(), 'BNT162b2', ARRAY['Pfizer-BioNTech', 'Comirnaty']),
    (gen_random_uuid(), 'ChAdOx1-S', ARRAY['AstraZeneca', 'Vaxzevria']),
    (gen_random_uuid(), 'Ad26.COV2.S', ARRAY['Johnson & Johnson', 'Janssen']),
    (gen_random_uuid(), 'NVX-CoV2373', ARRAY['Novavax', 'Nuvaxovid']),
    (gen_random_uuid(), 'Tetanus Toxoid', ARRAY['TTOX', 'Tetanus']),
    (gen_random_uuid(), 'Diphtheria Toxoid', ARRAY['DT', 'Diphtheria']),
    (gen_random_uuid(), 'Pertussis', ARRAY['Whooping Cough', 'Keuchhusten']),
    (gen_random_uuid(), 'Polio', ARRAY['IPV', 'Kinderlähmung']),
    (gen_random_uuid(), 'Haemophilus influenzae b', ARRAY['Hib', 'HIB']),
    (gen_random_uuid(), 'Hepatitis B', ARRAY['HepB', 'HBV']),
    (gen_random_uuid(), 'Measles', ARRAY['Masern', 'Rubeola']),
    (gen_random_uuid(), 'Mumps', ARRAY['Mumps', 'Ziegenpeter']),
    (gen_random_uuid(), 'Rubella', ARRAY['Röteln', 'German Measles']),
    (gen_random_uuid(), 'Varicella', ARRAY['Windpocken', 'Chickenpox']);

-- Insert vaccine types
INSERT INTO vaccine_type (id, name, code) VALUES
    (gen_random_uuid(), 'COVID-19 mRNA Moderna', 'COVID-MOD'),
    (gen_random_uuid(), 'COVID-19 mRNA Pfizer-BioNTech', 'COVID-PFZ'),
    (gen_random_uuid(), 'COVID-19 Vektor AstraZeneca', 'COVID-AZ'),
    (gen_random_uuid(), 'COVID-19 Vektor Johnson & Johnson', 'COVID-JJ'),
    (gen_random_uuid(), 'COVID-19 Protein Novavax', 'COVID-NVX'),
    (gen_random_uuid(), 'DTPa-IPV-Hib-HepB (6-fach)', 'DTPA-6'),
    (gen_random_uuid(), 'DTPa-IPV (4-fach)', 'DTPA-4'),
    (gen_random_uuid(), 'MMR (Masern-Mumps-Röteln)', 'MMR'),
    (gen_random_uuid(), 'MMRV (MMR + Varizellen)', 'MMRV'),
    (gen_random_uuid(), 'Tetanus-Diphtherie', 'TD'),
    (gen_random_uuid(), 'Varizellen (Windpocken)', 'VAR'),
    (gen_random_uuid(), 'Hepatitis B', 'HEPB');

-- Insert age categories
INSERT INTO age_category (id, name, age_min_days, age_max_days) VALUES
    (gen_random_uuid(), 'Saeuglinge (2-11 Monate)', 60, 335),      -- 2-11 Monate
    (gen_random_uuid(), 'Kleinkinder (1-2 Jahre)', 365, 730),     -- 1-2 Jahre
    (gen_random_uuid(), 'Vorschulkinder (3-5 Jahre)', 1095, 1825), -- 3-5 Jahre
    (gen_random_uuid(), 'Schulkinder (6-11 Jahre)', 2190, 4015),  -- 6-11 Jahre
    (gen_random_uuid(), 'Jugendliche (12-17 Jahre)', 4380, 6570), -- 12-17 Jahre
    (gen_random_uuid(), 'Junge Erwachsene (18-29 Jahre)', 6570, 10585), -- 18-29 Jahre
    (gen_random_uuid(), 'Erwachsene (30-39 Jahre)', 10950, 14235), -- 30-39 Jahre
    (gen_random_uuid(), 'Erwachsene (40-49 Jahre)', 14600, 17885), -- 40-49 Jahre
    (gen_random_uuid(), 'Erwachsene (50-59 Jahre)', 18250, 21535), -- 50-59 Jahre
    (gen_random_uuid(), 'Erwachsene (60-69 Jahre)', 21900, 25185), -- 60-69 Jahre
    (gen_random_uuid(), 'Senioren (70+ Jahre)', 25550, NULL);      -- 70+ Jahre (offen)

-- Link vaccine types with active substances
WITH vaccine_refs AS (
    SELECT id as vaccine_id, name as vaccine_name FROM vaccine_type
),
substance_refs AS (
    SELECT id as substance_id, name as substance_name FROM active_substance
)
INSERT INTO vaccine_type_active_substance (vaccine_type_id, active_substance_id, qualitative_amount)
SELECT
    v.vaccine_id,
    s.substance_id,
    '30 μg' as qualitative_amount
FROM vaccine_refs v, substance_refs s
WHERE v.vaccine_name = 'COVID-19 mRNA Moderna' AND s.substance_name = 'mRNA-1273'

UNION ALL

SELECT
    v.vaccine_id,
    s.substance_id,
    '30 μg' as qualitative_amount
FROM vaccine_refs v, substance_refs s
WHERE v.vaccine_name = 'COVID-19 mRNA Pfizer-BioNTech' AND s.substance_name = 'BNT162b2'

UNION ALL

SELECT
    v.vaccine_id,
    s.substance_id,
    '5×10¹⁰ Viruspartikel' as qualitative_amount
FROM vaccine_refs v, substance_refs s
WHERE v.vaccine_name = 'COVID-19 Vektor AstraZeneca' AND s.substance_name = 'ChAdOx1-S'

UNION ALL

SELECT
    v.vaccine_id,
    s.substance_id,
    '5×10¹⁰ Viruspartikel' as qualitative_amount
FROM vaccine_refs v, substance_refs s
WHERE v.vaccine_name = 'COVID-19 Vektor Johnson & Johnson' AND s.substance_name = 'Ad26.COV2.S'

UNION ALL

SELECT
    v.vaccine_id,
    s.substance_id,
    '5 μg' as qualitative_amount
FROM vaccine_refs v, substance_refs s
WHERE v.vaccine_name = 'COVID-19 Protein Novavax' AND s.substance_name = 'NVX-CoV2373';

-- Insert immunization plans for COVID-19 vaccines across different age groups
WITH vaccine_refs AS (
    SELECT id as vaccine_id, name as vaccine_name FROM vaccine_type
),
age_refs AS (
    SELECT id as age_id, name as age_name FROM age_category
)
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    'COVID-19 Grundimmunisierung - ' || a.age_name,
    v.vaccine_id,
    a.age_id
FROM vaccine_refs v, age_refs a
WHERE v.vaccine_name IN (
    'COVID-19 mRNA Moderna',
    'COVID-19 mRNA Pfizer-BioNTech'
) AND a.age_name IN (
    'Jugendliche (12-17 Jahre)',
    'Junge Erwachsene (18-29 Jahre)',
    'Erwachsene (30-39 Jahre)',
    'Erwachsene (40-49 Jahre)',
    'Erwachsene (50-59 Jahre)',
    'Erwachsene (60-69 Jahre)',
    'Senioren (70+ Jahre)'
);

-- Insert immunization plan series for COVID-19 basic immunization
WITH plan_refs AS (
    SELECT id as plan_id, name as plan_name FROM immunization_plan WHERE name LIKE 'COVID-19 Grundimmunisierung%'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'Grundimmunisierung',
    2
FROM plan_refs p;

-- Insert sample immunization plans for childhood vaccines
WITH vaccine_refs AS (
    SELECT id as vaccine_id, name as vaccine_name FROM vaccine_type
),
age_refs AS (
    SELECT id as age_id, name as age_name FROM age_category
)
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    '6-fach Impfung - ' || a.age_name,
    v.vaccine_id,
    a.age_id
FROM vaccine_refs v, age_refs a
WHERE v.vaccine_name = 'DTPa-IPV-Hib-HepB (6-fach)'
AND a.age_name IN ('Säuglinge (2-11 Monate)', 'Kleinkinder (1-2 Jahre)')

UNION ALL

SELECT
    gen_random_uuid(),
    'MMR Impfung - ' || a.age_name,
    v.vaccine_id,
    a.age_id
FROM vaccine_refs v, age_refs a
WHERE v.vaccine_name = 'MMR (Masern-Mumps-Röteln)'
AND a.age_name IN ('Kleinkinder (1-2 Jahre)', 'Vorschulkinder (3-5 Jahre)');

-- Insert immunization plan series for childhood vaccines
WITH plan_refs AS (
    SELECT id as plan_id, name as plan_name FROM immunization_plan
    WHERE name LIKE '6-fach Impfung%' OR name LIKE 'MMR Impfung%'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    CASE
        WHEN p.plan_name LIKE '6-fach Impfung%' THEN 'Grundimmunisierung 6-fach'
        WHEN p.plan_name LIKE 'MMR Impfung%' THEN 'MMR Grundimmunisierung'
    END,
    CASE
        WHEN p.plan_name LIKE '6-fach Impfung%' THEN 3
        WHEN p.plan_name LIKE 'MMR Impfung%' THEN 2
    END
FROM plan_refs p;

-- Insert a sample admin user (password: admin123)
INSERT INTO users (id, username, password_hash, first_name, last_name, birth_date, role) VALUES
    (gen_random_uuid(), 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjwvWvxbZb5aqjII7eJdG', 'System', 'Administrator', '1990-01-01', 'ADMIN');

-- Insert sample test users
INSERT INTO users (id, username, password_hash, first_name, last_name, birth_date, role) VALUES
    (gen_random_uuid(), 'john.doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjwvWvxbZb5aqjII7eJdG', 'John', 'Doe', '1985-05-15', 'USER'),
    (gen_random_uuid(), 'jane.smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjwvWvxbZb5aqjII7eJdG', 'Jane', 'Smith', '1992-03-22', 'USER'),
    (gen_random_uuid(), 'max.mustermann', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjwvWvxbZb5aqjII7eJdG', 'Max', 'Mustermann', '1978-11-08', 'USER');
