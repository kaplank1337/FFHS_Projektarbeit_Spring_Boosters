

INSERT INTO age_category (id, name, age_min_days, age_max_days)
SELECT gen_random_uuid(), 'Test - Faellig in 15 Tagen (<=30)', 9500, 20000
WHERE NOT EXISTS (SELECT 1 FROM age_category WHERE name = 'Test - Faellig in 15 Tagen (<=30)');

INSERT INTO age_category (id, name, age_min_days, age_max_days)
SELECT gen_random_uuid(), 'Test - Faellig in 60 Tagen (31-90)', 9545, 20000
WHERE NOT EXISTS (SELECT 1 FROM age_category WHERE name = 'Test - Faellig in 60 Tagen (31-90)');

INSERT INTO age_category (id, name, age_min_days, age_max_days)
SELECT gen_random_uuid(), 'Test - Ueberfaellig (ageMax < heute)', 9000, 9300
WHERE NOT EXISTS (SELECT 1 FROM age_category WHERE name = 'Test - Ueberfaellig (ageMax < heute)');


WITH v AS (
    SELECT id AS vaccine_type_id FROM vaccine_type WHERE code = 'TD'
),
a AS (
    SELECT id AS age_category_id FROM age_category WHERE name = 'Test - Faellig in 15 Tagen (<=30)'
)
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT gen_random_uuid(), 'TD Testplan - Faellig in 15 Tagen', v.vaccine_type_id, a.age_category_id
FROM v, a
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan WHERE name = 'TD Testplan - Faellig in 15 Tagen'
);

WITH v AS (
    SELECT id AS vaccine_type_id FROM vaccine_type WHERE code = 'VAR'
),
a AS (
    SELECT id AS age_category_id FROM age_category WHERE name = 'Test - Faellig in 60 Tagen (31-90)'
)
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT gen_random_uuid(), 'VAR Testplan - Faellig in 60 Tagen', v.vaccine_type_id, a.age_category_id
FROM v, a
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan WHERE name = 'VAR Testplan - Faellig in 60 Tagen'
);

WITH v AS (
    SELECT id AS vaccine_type_id FROM vaccine_type WHERE code = 'HEPB'
),
a AS (
    SELECT id AS age_category_id FROM age_category WHERE name = 'Test - Ueberfaellig (ageMax < heute)'
)
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT gen_random_uuid(), 'HEPB Testplan - Ueberfaellig', v.vaccine_type_id, a.age_category_id
FROM v, a
WHERE NOT EXISTS (
    SELECT 1 FROM immunization_plan WHERE name = 'HEPB Testplan - Ueberfaellig'
);


WITH plan_refs AS (
    SELECT id AS plan_id, name AS plan_name
    FROM immunization_plan
    WHERE name IN (
        'TD Testplan - Faellig in 15 Tagen',
        'VAR Testplan - Faellig in 60 Tagen',
        'HEPB Testplan - Ueberfaellig'
    )
)
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT gen_random_uuid(), p.plan_id, 'Testserie', 2
FROM plan_refs p
WHERE NOT EXISTS (
    SELECT 1
    FROM immunization_plan_series s
    WHERE s.immunization_plan_id = p.plan_id AND s.series_name = 'Testserie'
);
