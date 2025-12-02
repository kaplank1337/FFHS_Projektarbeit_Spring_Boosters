-- V4__Insert_additional_immunization_plans.sql
-- Ergänzt fehlende Impfpläne (immunization_plan) und Serien (immunization_plan_series)
-- für alle bereits vorhandenen Impfstoffe in vaccine_type.

------------------------------------------------------------
-- 1) COVID-19 Vektor- und Protein-Impfstoffe:
--    Grundimmunisierung für Jugendliche und Erwachsene
------------------------------------------------------------
WITH vaccine_refs AS (
    SELECT id AS vaccine_id, name AS vaccine_name
    FROM vaccine_type
),
     age_refs AS (
         SELECT id AS age_id, name AS age_name
         FROM age_category
     ),
     new_covid_vplans AS (
         SELECT 'COVID-19 Vektor AstraZeneca'::text      AS vaccine_name,
                'COVID-19 Grundimmunisierung - ' ||
                'Jugendliche (12-17 Jahre)'              AS plan_name,
                'Jugendliche (12-17 Jahre)'::text        AS age_name
         UNION ALL
         SELECT 'COVID-19 Vektor AstraZeneca',
                'COVID-19 Grundimmunisierung - ' || 'Junge Erwachsene (18-29 Jahre)',
                'Junge Erwachsene (18-29 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor AstraZeneca',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (30-39 Jahre)',
                'Erwachsene (30-39 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor AstraZeneca',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (40-49 Jahre)',
                'Erwachsene (40-49 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor AstraZeneca',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (50-59 Jahre)',
                'Erwachsene (50-59 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor AstraZeneca',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (60-69 Jahre)',
                'Erwachsene (60-69 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor AstraZeneca',
                'COVID-19 Grundimmunisierung - ' || 'Senioren (70+ Jahre)',
                'Senioren (70+ Jahre)'

         UNION ALL
         SELECT 'COVID-19 Vektor Johnson & Johnson',
                'COVID-19 Grundimmunisierung - ' || 'Junge Erwachsene (18-29 Jahre)',
                'Junge Erwachsene (18-29 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor Johnson & Johnson',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (30-39 Jahre)',
                'Erwachsene (30-39 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor Johnson & Johnson',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (40-49 Jahre)',
                'Erwachsene (40-49 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor Johnson & Johnson',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (50-59 Jahre)',
                'Erwachsene (50-59 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor Johnson & Johnson',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (60-69 Jahre)',
                'Erwachsene (60-69 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Vektor Johnson & Johnson',
                'COVID-19 Grundimmunisierung - ' || 'Senioren (70+ Jahre)',
                'Senioren (70+ Jahre)'

         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Jugendliche (12-17 Jahre)',
                'Jugendliche (12-17 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Junge Erwachsene (18-29 Jahre)',
                'Junge Erwachsene (18-29 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (30-39 Jahre)',
                'Erwachsene (30-39 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (40-49 Jahre)',
                'Erwachsene (40-49 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (50-59 Jahre)',
                'Erwachsene (50-59 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Erwachsene (60-69 Jahre)',
                'Erwachsene (60-69 Jahre)'
         UNION ALL
         SELECT 'COVID-19 Protein Novavax',
                'COVID-19 Grundimmunisierung - ' || 'Senioren (70+ Jahre)',
                'Senioren (70+ Jahre)'
     )
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    np.plan_name,
    v.vaccine_id,
    a.age_id
FROM new_covid_vplans np
         JOIN vaccine_refs v ON v.vaccine_name = np.vaccine_name
         JOIN age_refs    a ON a.age_name     = np.age_name
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.vaccine_id
      AND ip.age_category_id = a.age_id
      AND ip.name            = np.plan_name
);

-- Serien für diese COVID-19 Vektor/Protein-Grundimmunisierungen (2 Dosen)
WITH plan_refs AS (
    SELECT ip.id AS plan_id
    FROM immunization_plan ip
             JOIN vaccine_type vt ON vt.id = ip.vaccine_type_id
    WHERE ip.name LIKE 'COVID-19 Grundimmunisierung - %'
      AND vt.name IN (
                      'COVID-19 Vektor AstraZeneca',
                      'COVID-19 Vektor Johnson & Johnson',
                      'COVID-19 Protein Novavax'
        )
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'COVID-19 Grundimmunisierung',
    2
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips
    WHERE ips.immunization_plan_id = p.plan_id
);

------------------------------------------------------------
-- 2) DTPa-IPV (4-fach): Auffrischimpfungen in Kindheit/Jugend
------------------------------------------------------------
WITH vaccine_refs AS (
    SELECT id AS vaccine_id, name AS vaccine_name
    FROM vaccine_type
),
     age_refs AS (
         SELECT id AS age_id, name AS age_name
         FROM age_category
     ),
     new_dtpa4_plans AS (
         SELECT 'DTPa-IPV (4-fach)'::text              AS vaccine_name,
                'DTPa-IPV Auffrischimpfung - Vorschulkinder (3-5 Jahre)'::text AS plan_name,
                'Vorschulkinder (3-5 Jahre)'::text     AS age_name
         UNION ALL
         SELECT 'DTPa-IPV (4-fach)',
                'DTPa-IPV Auffrischimpfung - Schulkinder (6-11 Jahre)',
                'Schulkinder (6-11 Jahre)'
         UNION ALL
         SELECT 'DTPa-IPV (4-fach)',
                'DTPa-IPV Auffrischimpfung - Jugendliche (12-17 Jahre)',
                'Jugendliche (12-17 Jahre)'
     )
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    np.plan_name,
    v.vaccine_id,
    a.age_id
FROM new_dtpa4_plans np
         JOIN vaccine_refs v ON v.vaccine_name = np.vaccine_name
         JOIN age_refs    a ON a.age_name     = np.age_name
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.vaccine_id
      AND ip.age_category_id = a.age_id
      AND ip.name            = np.plan_name
);

-- Serien für DTPa-IPV (4-fach) Auffrischimpfungen (1 Dosis)
WITH plan_refs AS (
    SELECT ip.id AS plan_id
    FROM immunization_plan ip
             JOIN vaccine_type vt ON vt.id = ip.vaccine_type_id
    WHERE vt.name = 'DTPa-IPV (4-fach)'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'DTPa-IPV Auffrischimpfung',
    1
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips
    WHERE ips.immunization_plan_id = p.plan_id
);

------------------------------------------------------------
-- 3) MMRV (MMR + Varizellen): 2-Dosen-Schema im Kleinkindalter
------------------------------------------------------------
WITH vaccine_refs AS (
    SELECT id AS vaccine_id, name AS vaccine_name
    FROM vaccine_type
),
     age_refs AS (
         SELECT id AS age_id, name AS age_name
         FROM age_category
     ),
     new_mmrv_plans AS (
         SELECT 'MMRV (MMR + Varizellen)'::text AS vaccine_name,
                'MMRV Impfung - Kleinkinder (1-2 Jahre)'::text AS plan_name,
                'Kleinkinder (1-2 Jahre)'::text AS age_name
         UNION ALL
         SELECT 'MMRV (MMR + Varizellen)',
                'MMRV Impfung - Vorschulkinder (3-5 Jahre)',
                'Vorschulkinder (3-5 Jahre)'
     )
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    np.plan_name,
    v.vaccine_id,
    a.age_id
FROM new_mmrv_plans np
         JOIN vaccine_refs v ON v.vaccine_name = np.vaccine_name
         JOIN age_refs    a ON a.age_name     = np.age_name
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.vaccine_id
      AND ip.age_category_id = a.age_id
      AND ip.name            = np.plan_name
);

-- Serien für MMRV (2 Dosen)
WITH plan_refs AS (
    SELECT ip.id AS plan_id
    FROM immunization_plan ip
             JOIN vaccine_type vt ON vt.id = ip.vaccine_type_id
    WHERE vt.name = 'MMRV (MMR + Varizellen)'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'MMRV Grundimmunisierung',
    2
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips
    WHERE ips.immunization_plan_id = p.plan_id
);

------------------------------------------------------------
-- 4) Tetanus-Diphtherie (Td): Auffrischimpfungen im Erwachsenenalter
------------------------------------------------------------
WITH vaccine_refs AS (
    SELECT id AS vaccine_id, name AS vaccine_name
    FROM vaccine_type
),
     age_refs AS (
         SELECT id AS age_id, name AS age_name
         FROM age_category
     ),
     new_td_plans AS (
         SELECT 'Tetanus-Diphtherie'::text AS vaccine_name,
                'Td Auffrischimpfung - Junge Erwachsene (18-29 Jahre)'::text AS plan_name,
                'Junge Erwachsene (18-29 Jahre)'::text AS age_name
         UNION ALL
         SELECT 'Tetanus-Diphtherie',
                'Td Auffrischimpfung - Erwachsene (30-39 Jahre)',
                'Erwachsene (30-39 Jahre)'
         UNION ALL
         SELECT 'Tetanus-Diphtherie',
                'Td Auffrischimpfung - Erwachsene (40-49 Jahre)',
                'Erwachsene (40-49 Jahre)'
         UNION ALL
         SELECT 'Tetanus-Diphtherie',
                'Td Auffrischimpfung - Erwachsene (50-59 Jahre)',
                'Erwachsene (50-59 Jahre)'
         UNION ALL
         SELECT 'Tetanus-Diphtherie',
                'Td Auffrischimpfung - Erwachsene (60-69 Jahre)',
                'Erwachsene (60-69 Jahre)'
         UNION ALL
         SELECT 'Tetanus-Diphtherie',
                'Td Auffrischimpfung - Senioren (70+ Jahre)',
                'Senioren (70+ Jahre)'
     )
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    np.plan_name,
    v.vaccine_id,
    a.age_id
FROM new_td_plans np
         JOIN vaccine_refs v ON v.vaccine_name = np.vaccine_name
         JOIN age_refs    a ON a.age_name     = np.age_name
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.vaccine_id
      AND ip.age_category_id = a.age_id
      AND ip.name            = np.plan_name
);

-- Serien für Td-Auffrischimpfungen (1 Dosis)
WITH plan_refs AS (
    SELECT ip.id AS plan_id
    FROM immunization_plan ip
             JOIN vaccine_type vt ON vt.id = ip.vaccine_type_id
    WHERE vt.name = 'Tetanus-Diphtherie'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'Td Auffrischimpfung',
    1
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips
    WHERE ips.immunization_plan_id = p.plan_id
);

------------------------------------------------------------
-- 5) Varizellen (Windpocken): 2-Dosen-Schema + Nachholimpfungen
------------------------------------------------------------
WITH vaccine_refs AS (
    SELECT id AS vaccine_id, name AS vaccine_name
    FROM vaccine_type
),
     age_refs AS (
         SELECT id AS age_id, name AS age_name
         FROM age_category
     ),
     new_var_plans AS (
         SELECT 'Varizellen (Windpocken)'::text AS vaccine_name,
                'Varizellen Impfung - Kleinkinder (1-2 Jahre)'::text AS plan_name,
                'Kleinkinder (1-2 Jahre)'::text AS age_name
         UNION ALL
         SELECT 'Varizellen (Windpocken)',
                'Varizellen Impfung - Vorschulkinder (3-5 Jahre)',
                'Vorschulkinder (3-5 Jahre)'
         UNION ALL
         SELECT 'Varizellen (Windpocken)',
                'Varizellen Impfung - Schulkinder (6-11 Jahre)',
                'Schulkinder (6-11 Jahre)'
         UNION ALL
         SELECT 'Varizellen (Windpocken)',
                'Varizellen Impfung - Jugendliche (12-17 Jahre)',
                'Jugendliche (12-17 Jahre)'
     )
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    np.plan_name,
    v.vaccine_id,
    a.age_id
FROM new_var_plans np
         JOIN vaccine_refs v ON v.vaccine_name = np.vaccine_name
         JOIN age_refs    a ON a.age_name     = np.age_name
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.vaccine_id
      AND ip.age_category_id = a.age_id
      AND ip.name            = np.plan_name
);

-- Serien für Varizellen (2 Dosen)
WITH plan_refs AS (
    SELECT ip.id AS plan_id
    FROM immunization_plan ip
             JOIN vaccine_type vt ON vt.id = ip.vaccine_type_id
    WHERE vt.name = 'Varizellen (Windpocken)'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'Varizellen Grundimmunisierung',
    2
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips
    WHERE ips.immunization_plan_id = p.plan_id
);

------------------------------------------------------------
-- 6) Hepatitis B (Monopräparat): Nachhol- und Risikoimpfungen
------------------------------------------------------------
WITH vaccine_refs AS (
    SELECT id AS vaccine_id, name AS vaccine_name
    FROM vaccine_type
),
     age_refs AS (
         SELECT id AS age_id, name AS age_name
         FROM age_category
     ),
     new_hepb_plans AS (
         SELECT 'Hepatitis B'::text AS vaccine_name,
                'Hepatitis-B Impfung - Saeuglinge (2-11 Monate)'::text AS plan_name,
                'Saeuglinge (2-11 Monate)'::text AS age_name
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Jugendliche (12-17 Jahre)',
                'Jugendliche (12-17 Jahre)'
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Junge Erwachsene (18-29 Jahre)',
                'Junge Erwachsene (18-29 Jahre)'
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Erwachsene (30-39 Jahre)',
                'Erwachsene (30-39 Jahre)'
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Erwachsene (40-49 Jahre)',
                'Erwachsene (40-49 Jahre)'
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Erwachsene (50-59 Jahre)',
                'Erwachsene (50-59 Jahre)'
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Erwachsene (60-69 Jahre)',
                'Erwachsene (60-69 Jahre)'
         UNION ALL
         SELECT 'Hepatitis B',
                'Hepatitis-B Impfung - Senioren (70+ Jahre)',
                'Senioren (70+ Jahre)'
     )
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    np.plan_name,
    v.vaccine_id,
    a.age_id
FROM new_hepb_plans np
         JOIN vaccine_refs v ON v.vaccine_name = np.vaccine_name
         JOIN age_refs    a ON a.age_name     = np.age_name
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.vaccine_id
      AND ip.age_category_id = a.age_id
      AND ip.name            = np.plan_name
);

-- Serien für Hepatitis B (3 Dosen)
WITH plan_refs AS (
    SELECT ip.id AS plan_id
    FROM immunization_plan ip
             JOIN vaccine_type vt ON vt.id = ip.vaccine_type_id
    WHERE vt.name = 'Hepatitis B'
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    p.plan_id,
    'Hepatitis-B Grundimmunisierung',
    3
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips
    WHERE ips.immunization_plan_id = p.plan_id
);