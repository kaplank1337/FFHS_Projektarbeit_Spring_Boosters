# Spring Boosters - Testdaten für Demo
## Vorgefertigte Daten für Live-Präsentation

---

## 📋 Inhaltsverzeichnis
1. [Test-Benutzer mit vollständigen Daten](#test-benutzer)
2. [SQL-Queries für Stammdaten-Demo](#sql-queries)
3. [API-Requests (cURL)](#api-requests)
4. [Swagger-UI Test-Scenarios](#swagger-ui-tests)

---

## 1. Test-Benutzer mit vollständigen Daten

### Benutzer 1: Anna Müller (Junge Erwachsene, 23 Jahre)

**Registrierungs-Daten:**
```json
{
  "username": "anna_mueller",
  "firstName": "Anna",
  "lastName": "Müller",
  "birthDate": "2000-05-15",
  "email": "anna.mueller@example.com",
  "password": "secure123"
}
```

**Impfungen zum Hinzufügen:**

1. **COVID-19 Pfizer - Dosis 1**
   - Impfstofftyp: `COVID-19 mRNA Pfizer-BioNTech`
   - Datum: `2023-01-15`
   - Dosis: `1`

2. **COVID-19 Pfizer - Dosis 2**
   - Impfstofftyp: `COVID-19 mRNA Pfizer-BioNTech`
   - Datum: `2023-02-15`
   - Dosis: `2`

3. **COVID-19 Pfizer - Dosis 3 (Booster)**
   - Impfstofftyp: `COVID-19 mRNA Pfizer-BioNTech`
   - Datum: `2023-08-15`
   - Dosis: `3`

4. **MMR (aus Kindheit)**
   - Impfstofftyp: `MMR (Masern-Mumps-Röteln)`
   - Datum: `2001-06-01`
   - Dosis: `1`

5. **MMR Booster**
   - Impfstofftyp: `MMR (Masern-Mumps-Röteln)`
   - Datum: `2006-05-15`
   - Dosis: `2`

6. **Tetanus-Diphtherie (letzte Auffrischung)**
   - Impfstofftyp: `Tetanus-Diphtherie`
   - Datum: `2020-03-10`
   - Dosis: `1`

**Erwartete ausstehende Impfungen:**
- Tetanus-Diphtherie Auffrischung (überfällig, war 2020)
- COVID-19 Booster (je nach aktuellem Datum)

---

### Benutzer 2: Baby Schmidt (Säugling, 6 Monate)

**Registrierungs-Daten:**
```json
{
  "username": "baby_schmidt",
  "firstName": "Max",
  "lastName": "Schmidt",
  "birthDate": "2023-07-14",
  "email": "baby.schmidt@example.com",
  "password": "secure123"
}
```

**Impfungen zum Hinzufügen:**

1. **6-fach - Dosis 1 (mit 2 Monaten)**
   - Impfstofftyp: `DTPa-IPV-Hib-HepB (6-fach)`
   - Datum: `2023-09-14`
   - Dosis: `1`

2. **6-fach - Dosis 2 (mit 4 Monaten)**
   - Impfstofftyp: `DTPa-IPV-Hib-HepB (6-fach)`
   - Datum: `2023-11-14`
   - Dosis: `2`

**Erwartete ausstehende Impfungen:**
- 6-fach Dosis 3 (mit 6 Monaten, jetzt fällig!)
- 6-fach Dosis 4 (mit 15-18 Monaten)
- MMR (mit 12 Monaten)
- Varizellen (mit 12 Monaten)

---

### Benutzer 3: Klaus Werner (Senior, 75 Jahre)

**Registrierungs-Daten:**
```json
{
  "username": "senior_werner",
  "firstName": "Klaus",
  "lastName": "Werner",
  "birthDate": "1948-03-15",
  "email": "klaus.werner@example.com",
  "password": "secure123"
}
```

**Impfungen zum Hinzufügen:**

1. **COVID-19 Moderna - Dosis 1**
   - Impfstofftyp: `COVID-19 mRNA Moderna`
   - Datum: `2021-03-10`
   - Dosis: `1`

2. **COVID-19 Moderna - Dosis 2**
   - Impfstofftyp: `COVID-19 mRNA Moderna`
   - Datum: `2021-04-10`
   - Dosis: `2`

3. **COVID-19 Booster 1**
   - Impfstofftyp: `COVID-19 mRNA Moderna`
   - Datum: `2021-10-15`
   - Dosis: `3`

4. **COVID-19 Booster 2**
   - Impfstofftyp: `COVID-19 mRNA Moderna`
   - Datum: `2022-10-15`
   - Dosis: `4`

5. **Tetanus-Diphtherie (letzte Auffrischung)**
   - Impfstofftyp: `Tetanus-Diphtherie`
   - Datum: `2018-05-20`
   - Dosis: `1`

**Erwartete ausstehende Impfungen:**
- COVID-19 Booster (jährlich empfohlen für Senioren)
- Tetanus-Diphtherie Auffrischung (überfällig, war 2018)
- Influenza (jährlich, falls implementiert)
- Pneumokokken (einmalig, falls implementiert)

---

### Benutzer 4: Lisa Jung (Schulkind, 8 Jahre)

**Registrierungs-Daten:**
```json
{
  "username": "lisa_jung",
  "firstName": "Lisa",
  "lastName": "Jung",
  "birthDate": "2015-09-20",
  "email": "lisa.jung@example.com",
  "password": "secure123"
}
```

**Impfungen zum Hinzufügen:**

1. **6-fach - Dosis 1**
   - Impfstofftyp: `DTPa-IPV-Hib-HepB (6-fach)`
   - Datum: `2015-11-20`
   - Dosis: `1`

2. **6-fach - Dosis 2**
   - Impfstofftyp: `DTPa-IPV-Hib-HepB (6-fach)`
   - Datum: `2016-01-20`
   - Dosis: `2`

3. **6-fach - Dosis 3**
   - Impfstofftyp: `DTPa-IPV-Hib-HepB (6-fach)`
   - Datum: `2016-03-20`
   - Dosis: `3`

4. **6-fach - Dosis 4**
   - Impfstofftyp: `DTPa-IPV-Hib-HepB (6-fach)`
   - Datum: `2017-03-20`
   - Dosis: `4`

5. **MMR - Dosis 1**
   - Impfstofftyp: `MMR (Masern-Mumps-Röteln)`
   - Datum: `2016-09-20`
   - Dosis: `1`

6. **MMR - Dosis 2**
   - Impfstofftyp: `MMR (Masern-Mumps-Röteln)`
   - Datum: `2020-09-20`
   - Dosis: `2`

7. **4-fach Auffrischung**
   - Impfstofftyp: `DTPa-IPV (4-fach)`
   - Datum: `2020-09-20`
   - Dosis: `1`

**Erwartete ausstehende Impfungen:**
- Varizellen (falls nicht geimpft)
- HPV (ab 11-12 Jahren empfohlen, falls implementiert)

---

## 2. SQL-Queries für Stammdaten-Demo

### PostgreSQL direkt abfragen (in Docker)

```bash
# Shell in PostgreSQL-Container öffnen
docker exec -it postgres psql -U postgres -d spring_booster_db

# Oder direkter Query:
docker exec -it postgres psql -U postgres -d spring_booster_db -c "SELECT * FROM vaccine_type;"
```

---

### Query 1: Alle Impfstofftypen anzeigen

```sql
SELECT
    name AS "Impfstoffname",
    code AS "Code",
    created_at AS "Erstellt am"
FROM vaccine_type
ORDER BY name;
```

**Erwartete Ausgabe (12 Zeilen):**
```
Impfstoffname                          | Code              | Erstellt am
---------------------------------------|-------------------|-------------------
COVID-19 mRNA Moderna                  | COVID-MOD         | 2024-01-01 10:00:00
COVID-19 mRNA Pfizer-BioNTech          | COVID-PFZ         | 2024-01-01 10:00:00
COVID-19 Protein Novavax               | COVID-NVX         | 2024-01-01 10:00:00
COVID-19 Vektor AstraZeneca            | COVID-AZ          | 2024-01-01 10:00:00
COVID-19 Vektor Johnson & Johnson      | COVID-JJ          | 2024-01-01 10:00:00
DTPa-IPV (4-fach)                      | DTPA-IPV          | 2024-01-01 10:00:00
DTPa-IPV-Hib-HepB (6-fach)             | DTPA-IPV-HIB-HEPB | 2024-01-01 10:00:00
Hepatitis B                            | HEPB              | 2024-01-01 10:00:00
MMR (Masern-Mumps-Röteln)              | MMR               | 2024-01-01 10:00:00
MMRV (MMR + Varizellen)                | MMRV              | 2024-01-01 10:00:00
Tetanus-Diphtherie                     | TD                | 2024-01-01 10:00:00
Varizellen (Windpocken)                | VAR               | 2024-01-01 10:00:00
```

---

### Query 2: Alterskategorien mit Altersbereich

```sql
SELECT
    name AS "Kategorie",
    age_min_days AS "Min Tage",
    age_max_days AS "Max Tage",
    CONCAT(
        FLOOR(age_min_days / 365),
        ' - ',
        CASE
            WHEN age_max_days IS NULL THEN '∞'
            ELSE FLOOR(age_max_days / 365)::TEXT
        END,
        ' Jahre'
    ) AS "Altersbereich"
FROM age_category
ORDER BY age_min_days;
```

**Erwartete Ausgabe (11 Zeilen):**
```
Kategorie                        | Min Tage | Max Tage | Altersbereich
---------------------------------|----------|----------|---------------
Säuglinge (2-11 Monate)          | 60       | 335      | 0 - 0 Jahre
Kleinkinder (1-2 Jahre)          | 365      | 730      | 1 - 2 Jahre
Vorschulkinder (3-5 Jahre)       | 1095     | 1825     | 3 - 5 Jahre
Schulkinder (6-11 Jahre)         | 2190     | 4015     | 6 - 11 Jahre
Jugendliche (12-17 Jahre)        | 4380     | 6570     | 12 - 18 Jahre
Junge Erwachsene (18-29 Jahre)   | 6570     | 10585    | 18 - 29 Jahre
Erwachsene (30-39 Jahre)         | 10950    | 14235    | 30 - 38 Jahre
Erwachsene (40-49 Jahre)         | 14600    | 17885    | 40 - 48 Jahre
Erwachsene (50-59 Jahre)         | 18250    | 21535    | 50 - 58 Jahre
Erwachsene (60-69 Jahre)         | 21900    | 25185    | 60 - 68 Jahre
Senioren (70+ Jahre)             | 25550    | NULL     | 70 - ∞ Jahre
```

---

### Query 3: Wirkstoffe mit Synonymen

```sql
SELECT
    name AS "Wirkstoff",
    ARRAY_TO_STRING(synonyms, ', ') AS "Synonyme"
FROM active_substance
ORDER BY name;
```

**Erwartete Ausgabe (15 Zeilen):**
```
Wirkstoff                     | Synonyme
------------------------------|----------------------------------
BNT162b2                      | Pfizer mRNA, Comirnaty
ChAdOx1-S                     | Vaxzevria
Diphtheria Toxoid             | Diphtherie-Impfstoff
Haemophilus influenzae b      | Hib-Impfstoff
Hepatitis B                   | HBV-Impfstoff
Measles                       | Masern-Impfstoff
Mumps                         | Mumps-Impfstoff
NVX-CoV2373                   | Novavax
Pertussis                     | Keuchhusten-Impfstoff
Polio                         | Poliomyelitis-Impfstoff
Rubella                       | Röteln-Impfstoff
Tetanus Toxoid                | Tetanus-Impfstoff
Varicella                     | Windpocken-Impfstoff
mRNA-1273                     | Moderna mRNA, Spikevax
```

---

### Query 4: 6-fach Impfstoff - Wirkstoffe anzeigen

```sql
SELECT
    vt.name AS "Impfstoff",
    as2.name AS "Wirkstoff",
    vtas.amount AS "Menge"
FROM vaccine_type vt
JOIN vaccine_type_active_substance vtas ON vt.id = vtas.vaccine_type_id
JOIN active_substance as2 ON vtas.active_substance_id = as2.id
WHERE vt.code = 'DTPA-IPV-HIB-HEPB'
ORDER BY as2.name;
```

**Erwartete Ausgabe (6 Zeilen):**
```
Impfstoff                      | Wirkstoff                 | Menge
-------------------------------|---------------------------|-------
DTPa-IPV-Hib-HepB (6-fach)     | Diphtheria Toxoid         | 1.0
DTPa-IPV-Hib-HepB (6-fach)     | Haemophilus influenzae b  | 1.0
DTPa-IPV-Hib-HepB (6-fach)     | Hepatitis B               | 1.0
DTPa-IPV-Hib-HepB (6-fach)     | Pertussis                 | 1.0
DTPa-IPV-Hib-HepB (6-fach)     | Polio                     | 1.0
DTPa-IPV-Hib-HepB (6-fach)     | Tetanus Toxoid            | 1.0
```

---

### Query 5: Impfpläne mit Serien

```sql
SELECT
    vt.name AS "Impfstoff",
    ac.name AS "Alterskategorie",
    ips.series_name AS "Serie",
    ips.required_doses AS "Erforderliche Dosen"
FROM immunization_plan ip
JOIN vaccine_type vt ON ip.vaccine_type_id = vt.id
JOIN age_category ac ON ip.age_category_id = ac.id
LEFT JOIN immunization_plan_series ips ON ip.id = ips.immunization_plan_id
WHERE vt.code = 'DTPA-IPV-HIB-HEPB'
ORDER BY ac.age_min_days, ips.series_name;
```

**Erwartete Ausgabe:**
```
Impfstoff                      | Alterskategorie           | Serie               | Erforderliche Dosen
-------------------------------|---------------------------|---------------------|--------------------
DTPa-IPV-Hib-HepB (6-fach)     | Säuglinge (2-11 Monate)   | Grundimmunisierung  | 4
```

---

### Query 6: Follow-Up-Rules anzeigen

```sql
SELECT
    vt_from.name AS "Von Impfstoff",
    ac_from.name AS "Von Alterskategorie",
    vt_to.name AS "Nach Impfstoff",
    ac_to.name AS "Nach Alterskategorie",
    fur.min_completed_doses AS "Min abgeschlossene Dosen",
    fur.min_interval_days_since_last AS "Min Abstand (Tage)"
FROM follow_up_rule fur
JOIN immunization_plan ip_from ON fur.from_plan_id = ip_from.id
JOIN immunization_plan ip_to ON fur.to_plan_id = ip_to.id
JOIN vaccine_type vt_from ON ip_from.vaccine_type_id = vt_from.id
JOIN vaccine_type vt_to ON ip_to.vaccine_type_id = vt_to.id
JOIN age_category ac_from ON ip_from.age_category_id = ac_from.id
JOIN age_category ac_to ON ip_to.age_category_id = ac_to.id
LIMIT 5;
```

---

### Query 7: Benutzer mit Alter berechnen

```sql
SELECT
    username,
    first_name,
    last_name,
    birth_date,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, birth_date)) AS "Alter (Jahre)",
    CURRENT_DATE - birth_date AS "Alter (Tage)"
FROM users
ORDER BY birth_date DESC;
```

---

### Query 8: Impfungen eines Benutzers

```sql
SELECT
    u.username AS "Benutzer",
    vt.name AS "Impfstoff",
    ir.administered_on AS "Verabreichungsdatum",
    ir.dose_order_claimed AS "Dosis"
FROM immunization_record ir
JOIN users u ON ir.user_id = u.id
JOIN vaccine_type vt ON ir.vaccine_type_id = vt.id
WHERE u.username = 'anna_mueller'
ORDER BY ir.administered_on;
```

---

## 3. API-Requests (cURL)

### Login und Token abrufen

```bash
# Login
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "anna_mueller",
    "password": "secure123"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "userId": "550e8400-e29b-41d4-a716-446655440000",
#   "username": "anna_mueller"
# }

# Token speichern
export TOKEN="<token_aus_response>"
```

---

### Alle Impfstofftypen abrufen

```bash
curl -X GET http://localhost:8000/api/v1/vaccine-types \
  -H "Authorization: Bearer $TOKEN"
```

---

### Meine Impfungen abrufen

```bash
curl -X GET http://localhost:8000/api/v1/immunization-records/myVaccinations \
  -H "Authorization: Bearer $TOKEN"
```

---

### Ausstehende Impfungen abrufen

```bash
# Alle ausstehenden Impfungen
curl -X GET http://localhost:8000/api/v1/immunization-schedule/pending \
  -H "Authorization: Bearer $TOKEN"

# Zusammenfassung
curl -X GET http://localhost:8000/api/v1/immunization-schedule/pending/summary \
  -H "Authorization: Bearer $TOKEN"

# Nur überfällige
curl -X GET http://localhost:8000/api/v1/immunization-schedule/pending/overdue \
  -H "Authorization: Bearer $TOKEN"

# Nur in den nächsten 30 Tagen fällig
curl -X GET http://localhost:8000/api/v1/immunization-schedule/pending/due-soon \
  -H "Authorization: Bearer $TOKEN"

# Nur in 31-90 Tagen fällig
curl -X GET http://localhost:8000/api/v1/immunization-schedule/pending/upcoming \
  -H "Authorization: Bearer $TOKEN"
```

---

### Neue Impfung hinzufügen

```bash
# Erst Impfstofftyp-ID ermitteln (aus GET /vaccine-types)
# Dann:
curl -X POST http://localhost:8000/api/v1/immunization-records \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "vaccineTypeId": "550e8400-e29b-41d4-a716-446655440001",
    "administeredOn": "2024-01-14",
    "doseOrderClaimed": 1
  }'
```

---

## 4. Swagger-UI Test-Scenarios

### Öffnen: http://localhost:8000/swagger-ui/index.html

---

### Scenario 1: Registrierung und Login

1. **POST /api/v1/auth/register**
   - Request Body:
     ```json
     {
       "username": "test_user",
       "firstName": "Test",
       "lastName": "User",
       "birthDate": "1995-05-15",
       "email": "test@example.com",
       "password": "secure123"
     }
     ```
   - Expected: `201 Created`

2. **POST /api/v1/auth/login**
   - Request Body:
     ```json
     {
       "username": "test_user",
       "password": "secure123"
     }
     ```
   - Expected: `200 OK` mit JWT-Token
   - **Token kopieren für nächste Requests!**

3. **GET /api/v1/auth/me**
   - Header: `Authorization: Bearer <token>`
   - Expected: `200 OK` mit Benutzerinfos

---

### Scenario 2: Impfstofftypen durchsuchen

1. **GET /api/v1/vaccine-types**
   - Expected: `200 OK` mit 12 Impfstofftypen

2. **GET /api/v1/vaccine-types/{id}**
   - Path-Parameter: ID aus vorheriger Response
   - Expected: `200 OK` mit Details

---

### Scenario 3: Alterskategorien analysieren

1. **GET /api/v1/age-categories**
   - Expected: `200 OK` mit 11 Kategorien

2. **Filtern nach Alter (manuell im Response):**
   - Suche Kategorie mit `ageMinDays <= 8400 <= ageMaxDays` (23 Jahre)
   - Ergebnis: "Junge Erwachsene (18-29 Jahre)"

---

### Scenario 4: Impfungen verwalten

1. **POST /api/v1/immunization-records**
   - Header: `Authorization: Bearer <token>`
   - Request Body:
     ```json
     {
       "vaccineTypeId": "<covid_pfizer_id>",
       "administeredOn": "2024-01-14",
       "doseOrderClaimed": 1
     }
     ```
   - Expected: `201 Created`

2. **GET /api/v1/immunization-records/myVaccinations**
   - Header: `Authorization: Bearer <token>`
   - Expected: `200 OK` mit neu hinzugefügter Impfung

3. **PATCH /api/v1/immunization-records/{id}**
   - Header: `Authorization: Bearer <token>`
   - Request Body:
     ```json
     {
       "administeredOn": "2024-01-15",
       "doseOrderClaimed": 1
     }
     ```
   - Expected: `200 OK`

4. **DELETE /api/v1/immunization-records/{id}**
   - Header: `Authorization: Bearer <token>`
   - Expected: `204 No Content`

---

### Scenario 5: Ausstehende Impfungen

1. **GET /api/v1/immunization-schedule/pending/summary**
   - Header: `Authorization: Bearer <token>`
   - Expected: `200 OK` mit:
     ```json
     {
       "userId": "...",
       "username": "test_user",
       "birthDate": "1995-05-15",
       "currentAgeDays": 10500,
       "totalPending": 5,
       "overdueCount": 1,
       "dueSoonCount": 2,
       "upcomingDueCount": 2
     }
     ```

2. **GET /api/v1/immunization-schedule/pending**
   - Header: `Authorization: Bearer <token>`
   - Expected: `200 OK` mit Liste aller ausstehenden Impfungen

---

## 5. Präsentations-Checkliste

- [ ] Docker-Container gestartet (`docker-compose up -d`)
- [ ] Frontend erreichbar (http://localhost:3000)
- [ ] Swagger-UI erreichbar (http://localhost:8000/swagger-ui)
- [ ] PostgreSQL läuft (`docker-compose ps`)
- [ ] Test-Benutzer vorbereitet (anna_mueller, baby_schmidt, senior_werner)
- [ ] SQL-Queries getestet (`docker exec -it postgres psql ...`)
- [ ] Browser-Tabs geöffnet:
  - Frontend (localhost:3000)
  - Swagger-UI (localhost:8000/swagger-ui)
  - Demo-Anleitung (DEMO_ANLEITUNG.md)
- [ ] Bildschirm-Auflösung optimiert (1920x1080 empfohlen)
- [ ] Browser-Zoom auf 100%

---

**Alles bereit für eine erfolgreiche Demo!** 🚀
