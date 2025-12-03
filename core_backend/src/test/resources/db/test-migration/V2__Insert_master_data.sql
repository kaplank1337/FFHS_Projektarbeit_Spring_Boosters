-- H2-compatible version of V2__Insert_master_data.sql

CREATE SCHEMA IF NOT EXISTS SPRING_BOOSTERS;
SET SCHEMA SPRING_BOOSTERS;

-- Insert default active substances
INSERT INTO active_substance (id, name, synonyms) VALUES
                                                      (RANDOM_UUID(), 'mRNA-1273', 'Moderna mRNA,Spikevax'),
                                                      (RANDOM_UUID(), 'BNT162b2', 'Pfizer-BioNTech,Comirnaty'),
                                                      (RANDOM_UUID(), 'ChAdOx1-S', 'AstraZeneca,Vaxzevria'),
                                                      (RANDOM_UUID(), 'Ad26.COV2.S', 'Johnson & Johnson,Janssen'),
                                                      (RANDOM_UUID(), 'NVX-CoV2373', 'Novavax,Nuvaxovid'),
                                                      (RANDOM_UUID(), 'Tetanus Toxoid', 'TTOX,Tetanus'),
                                                      (RANDOM_UUID(), 'Diphtheria Toxoid', 'DT,Diphtheria'),
                                                      (RANDOM_UUID(), 'Pertussis', 'Whooping Cough,Keuchhusten'),
                                                      (RANDOM_UUID(), 'Polio', 'IPV,Kinderlähmung'),
                                                      (RANDOM_UUID(), 'Haemophilus influenzae b', 'Hib,HIB'),
                                                      (RANDOM_UUID(), 'Hepatitis B', 'HepB,HBV'),
                                                      (RANDOM_UUID(), 'Measles', 'Masern,Rubeola'),
                                                      (RANDOM_UUID(), 'Mumps', 'Mumps,Ziegenpeter'),
                                                      (RANDOM_UUID(), 'Rubella', 'Röteln,German Measles'),
                                                      (RANDOM_UUID(), 'Varicella', 'Windpocken,Chickenpox');

-- Insert vaccine types
INSERT INTO vaccine_type (id, name, code) VALUES
                                              (RANDOM_UUID(), 'COVID-19 mRNA Moderna', 'COVID-MOD'),
                                              (RANDOM_UUID(), 'COVID-19 mRNA Pfizer-BioNTech', 'COVID-PFZ'),
                                              (RANDOM_UUID(), 'COVID-19 Vektor AstraZeneca', 'COVID-AZ'),
                                              (RANDOM_UUID(), 'COVID-19 Vektor Johnson & Johnson', 'COVID-JJ'),
                                              (RANDOM_UUID(), 'COVID-19 Protein Novavax', 'COVID-NVX'),
                                              (RANDOM_UUID(), 'DTPa-IPV-Hib-HepB (6-fach)', 'DTPA-6'),
                                              (RANDOM_UUID(), 'DTPa-IPV (4-fach)', 'DTPA-4'),
                                              (RANDOM_UUID(), 'MMR (Masern-Mumps-Röteln)', 'MMR'),
                                              (RANDOM_UUID(), 'MMRV (MMR + Varizellen)', 'MMRV'),
                                              (RANDOM_UUID(), 'Tetanus-Diphtherie', 'TD'),
                                              (RANDOM_UUID(), 'Varizellen (Windpocken)', 'VAR'),
                                              (RANDOM_UUID(), 'Hepatitis B', 'HEPB');

-- Insert age categories
INSERT INTO age_category (id, name, age_min_days, age_max_days) VALUES
                                                                    (RANDOM_UUID(), 'Saeuglinge (2-11 Monate)', 60, 335),
                                                                    (RANDOM_UUID(), 'Kleinkinder (1-2 Jahre)', 365, 730),
                                                                    (RANDOM_UUID(), 'Vorschulkinder (3-5 Jahre)', 1095, 1825),
                                                                    (RANDOM_UUID(), 'Schulkinder (6-11 Jahre)', 2190, 4015),
                                                                    (RANDOM_UUID(), 'Jugendliche (12-17 Jahre)', 4380, 6570),
                                                                    (RANDOM_UUID(), 'Junge Erwachsene (18-29 Jahre)', 6570, 10585),
                                                                    (RANDOM_UUID(), 'Erwachsene (30-39 Jahre)', 10950, 14235),
                                                                    (RANDOM_UUID(), 'Erwachsene (40-49 Jahre)', 14600, 17885),
                                                                    (RANDOM_UUID(), 'Erwachsene (50-59 Jahre)', 18250, 21535),
                                                                    (RANDOM_UUID(), 'Erwachsene (60-69 Jahre)', 21900, 25185),
                                                                    (RANDOM_UUID(), 'Senioren (70+ Jahre)', 25550, NULL);

-- Link vaccine types with active substances
INSERT INTO vaccine_type_active_substance (vaccine_type_id, active_substance_id, qualitative_amount)
SELECT v.id, s.id, '30 μg'
FROM vaccine_type v
         JOIN active_substance s ON v.name = 'COVID-19 mRNA Moderna' AND s.name = 'mRNA-1273'
UNION ALL
SELECT v.id, s.id, '30 μg'
FROM vaccine_type v
         JOIN active_substance s ON v.name = 'COVID-19 mRNA Pfizer-BioNTech' AND s.name = 'BNT162b2'
UNION ALL
SELECT v.id, s.id, '5×10¹⁰ Viruspartikel'
FROM vaccine_type v
         JOIN active_substance s ON v.name = 'COVID-19 Vektor AstraZeneca' AND s.name = 'ChAdOx1-S'
UNION ALL
SELECT v.id, s.id, '5×10¹⁰ Viruspartikel'
FROM vaccine_type v
         JOIN active_substance s ON v.name = 'COVID-19 Vektor Johnson & Johnson' AND s.name = 'Ad26.COV2.S'
UNION ALL
SELECT v.id, s.id, '5 μg'
FROM vaccine_type v
         JOIN active_substance s ON v.name = 'COVID-19 Protein Novavax' AND s.name = 'NVX-CoV2373';

-- Insert immunization plans for COVID-19 vaccines across different age groups
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT RANDOM_UUID(),
       'COVID-19 Grundimmunisierung - ' || a.name,
       v.id,
       a.id
FROM vaccine_type v,
     age_category a
WHERE v.name IN ('COVID-19 mRNA Moderna', 'COVID-19 mRNA Pfizer-BioNTech')
  AND a.name IN ('Jugendliche (12-17 Jahre)',
                 'Junge Erwachsene (18-29 Jahre)',
                 'Erwachsene (30-39 Jahre)',
                 'Erwachsene (40-49 Jahre)',
                 'Erwachsene (50-59 Jahre)',
                 'Erwachsene (60-69 Jahre)',
                 'Senioren (70+ Jahre)');

-- Insert immunization plan series for COVID-19 basic immunization
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT RANDOM_UUID(),
       ip.id,
       'Grundimmunisierung',
       2
FROM immunization_plan ip
WHERE ip.name LIKE 'COVID-19 Grundimmunisierung%';

-- Insert sample immunization plans for childhood vaccines
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT RANDOM_UUID(),
       '6-fach Impfung - ' || a.name,
       v.id,
       a.id
FROM vaccine_type v,
     age_category a
WHERE v.name = 'DTPa-IPV-Hib-HepB (6-fach)'
  AND a.name IN ('Saeuglinge (2-11 Monate)', 'Kleinkinder (1-2 Jahre)')
UNION ALL
SELECT RANDOM_UUID(),
       'MMR Impfung - ' || a.name,
       v.id,
       a.id
FROM vaccine_type v,
     age_category a
WHERE v.name = 'MMR (Masern-Mumps-Röteln)'
  AND a.name IN ('Kleinkinder (1-2 Jahre)', 'Vorschulkinder (3-5 Jahre)');

-- Insert immunization plan series for childhood vaccines
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT RANDOM_UUID(),
       ip.id,
       CASE
           WHEN ip.name LIKE '6-fach Impfung%' THEN 'Grundimmunisierung 6-fach'
           WHEN ip.name LIKE 'MMR Impfung%' THEN 'MMR Grundimmunisierung'
           END,
       CASE
           WHEN ip.name LIKE '6-fach Impfung%' THEN 3
           WHEN ip.name LIKE 'MMR Impfung%' THEN 2
           END
FROM immunization_plan ip
WHERE ip.name LIKE '6-fach Impfung%' OR ip.name LIKE 'MMR Impfung%';

-- Insert a sample admin user (password: admin123 - 12 Runden bcrypt)
-- NOTE: column list includes `email` and values are provided in the same order
INSERT INTO users (id, username, email, password_hash, first_name, last_name, birth_date, role) VALUES
    (RANDOM_UUID(), 'admin', 'admin@admin.ch',
     '$2a$12$kLzgXVPlkELs.4p/dYS7n.xzt9C8zUA3EmbFwilJyQa9jyVEtCaiC',
     'System', 'Administrator', DATE '1990-01-01', 'ADMIN');

-- Insert sample test users (password: user123 - 12 Runden bcrypt)
INSERT INTO users (id, username, email, password_hash, first_name, last_name, birth_date, role) VALUES
    (RANDOM_UUID(), 'john.doe', 'john.doe@example.com',
     '$2a$12$rKOaaufhZ0W6zB0Ic6gqwuvufTLJqZSbgkMKt7EaaKZADTvCR7Rb6',
     'John', 'Doe', DATE '1985-05-15', 'USER'),
    (RANDOM_UUID(), 'jane.smith', 'jane.smith@example.com',
     '$2a$12$rKOaaufhZ0W6zB0Ic6gqwuvufTLJqZSbgkMKt7EaaKZADTvCR7Rb6',
     'Jane', 'Smith', DATE '1992-03-22', 'USER'),
    (RANDOM_UUID(), 'max.mustermann', 'max.mustermann@example.com',
     '$2a$12$rKOaaufhZ0W6zB0Ic6gqwuvufTLJqZSbgkMKt7EaaKZADTvCR7Rb6',
     'Max', 'Mustermann', DATE '1978-11-08', 'USER');
