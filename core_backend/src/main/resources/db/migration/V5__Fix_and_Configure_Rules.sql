-- V5__Fix_and_Configure_Rules.sql

-- 1. Ensure 'Saeuglinge' naming consistency
UPDATE age_category SET name = 'Saeuglinge (2-11 Monate)' WHERE name = 'Säuglinge (2-11 Monate)';

-- 2. Ensure missing plans for 6-fach and Saeuglinge are created
INSERT INTO immunization_plan (id, name, vaccine_type_id, age_category_id)
SELECT
    gen_random_uuid(),
    '6-fach Impfung - ' || a.name,
    v.id,
    a.id
FROM vaccine_type v, age_category a
WHERE v.name = 'DTPa-IPV-Hib-HepB (6-fach)'
AND a.name = 'Saeuglinge (2-11 Monate)'
AND NOT EXISTS (
    SELECT 1 FROM immunization_plan ip
    WHERE ip.vaccine_type_id = v.id AND ip.age_category_id = a.id
);

-- Ensure Series exists for these plans
INSERT INTO immunization_plan_series (id, immunization_plan_id, series_name, required_doses)
SELECT
    gen_random_uuid(),
    ip.id,
    'Grundimmunisierung 6-fach',
    3
FROM immunization_plan ip
WHERE ip.name LIKE '6-fach Impfung - Saeuglinge%'
AND NOT EXISTS (
    SELECT 1 FROM immunization_plan_series ips WHERE ips.immunization_plan_id = ip.id
);

-- 3. Configure Intervals via Follow Up Rules

-- Rule 1: After 1st dose, wait 60 days (8 weeks) for the 2nd dose
WITH sixfold_plan_data AS (
    SELECT
        ip.id as plan_id,
        ips.id as series_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    JOIN immunization_plan_series ips ON ip.id = ips.immunization_plan_id
    WHERE vt.name = 'DTPa-IPV-Hib-HepB (6-fach)'
    AND ip.name LIKE '%Saeuglinge%'
    LIMIT 1
)
INSERT INTO follow_up_rule (
    id,
    from_plan_id,
    to_plan_id,
    required_series_id,
    min_completed_doses,
    min_interval_days_since_last,
    target_min_age_days
)
SELECT
    gen_random_uuid(),
    spd.plan_id,
    spd.plan_id,
    spd.series_id,
    1,   -- Trigger: After 1 dose is completed
    60,  -- Constraint: 60 days interval
    120  -- Target: Around 4 months old
FROM sixfold_plan_data spd
WHERE NOT EXISTS (
    SELECT 1 FROM follow_up_rule fr
    WHERE fr.from_plan_id = spd.plan_id AND fr.min_completed_doses = 1
);

-- Rule 2: After 2nd dose, wait 300 days (10 months) for the 3rd dose
WITH sixfold_plan_data AS (
    SELECT
        ip.id as plan_id,
        ips.id as series_id
    FROM immunization_plan ip
    JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
    JOIN immunization_plan_series ips ON ip.id = ips.immunization_plan_id
    WHERE vt.name = 'DTPa-IPV-Hib-HepB (6-fach)'
    AND ip.name LIKE '%Saeuglinge%'
    LIMIT 1
)
INSERT INTO follow_up_rule (
    id,
    from_plan_id,
    to_plan_id,
    required_series_id,
    min_completed_doses,
    min_interval_days_since_last,
    target_min_age_days
)
SELECT
    gen_random_uuid(),
    spd.plan_id,
    spd.plan_id,
    spd.series_id,
    2,   -- Trigger: After 2 doses are completed
    300, -- Constraint: 300 days interval
    330  -- Target: Around 11 months old
FROM sixfold_plan_data spd
WHERE NOT EXISTS (
    SELECT 1 FROM follow_up_rule fr
    WHERE fr.from_plan_id = spd.plan_id AND fr.min_completed_doses = 2
);
