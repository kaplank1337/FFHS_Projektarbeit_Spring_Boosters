# Spring Boosters - Demo Quick Reference
## Präsentations-Spickzettel (5 Minuten)

---

## 🚀 Schnellstart

```bash
docker-compose up -d
# Warten: 1-2 Minuten
# Öffnen: http://localhost:3000
```

---

## 👥 Test-Benutzer für Demo

### 1. Anna Müller (Junge Erwachsene, 23 Jahre)
```
Username: anna_mueller
Passwort: secure123
Geburtsdatum: 15.05.2000
E-Mail: anna.mueller@example.com
```
**Zeigt:** COVID-19, Tetanus-Diphtherie Auffrischungen

### 2. Baby Schmidt (Säugling, 6 Monate)
```
Username: baby_schmidt
Passwort: secure123
Geburtsdatum: 14.07.2023
E-Mail: baby.schmidt@example.com
```
**Zeigt:** 6-fach Impfungen, MMR (erste Dosis)

### 3. Klaus Werner (Senior, 75 Jahre)
```
Username: senior_werner
Passwort: secure123
Geburtsdatum: 15.03.1948
E-Mail: klaus.werner@example.com
```
**Zeigt:** COVID-19, Tetanus-Diphtherie, Influenza

---

## 📊 11 Alterskategorien (in Tagen!)

| Kategorie | Tage (Min-Max) | Jahre |
|-----------|----------------|-------|
| Säuglinge | 60-335 | 2-11 Monate |
| Kleinkinder | 365-730 | 1-2 Jahre |
| Vorschulkinder | 1.095-1.825 | 3-5 Jahre |
| Schulkinder | 2.190-4.015 | 6-11 Jahre |
| Jugendliche | 4.380-6.570 | 12-17 Jahre |
| Junge Erwachsene | 6.570-10.585 | 18-29 Jahre |
| Erwachsene 30-39 | 10.950-14.235 | 30-39 Jahre |
| Erwachsene 40-49 | 14.600-17.885 | 40-49 Jahre |
| Erwachsene 50-59 | 18.250-21.535 | 50-59 Jahre |
| Erwachsene 60-69 | 21.900-25.185 | 60-69 Jahre |
| Senioren 70+ | 25.550+ | 70+ Jahre |

---

## 💉 12 Impfstofftypen

### COVID-19 (5 Varianten)
- COVID-19 mRNA Moderna (COVID-MOD)
- COVID-19 mRNA Pfizer-BioNTech (COVID-PFZ)
- COVID-19 Vektor AstraZeneca (COVID-AZ)
- COVID-19 Vektor Johnson & Johnson (COVID-JJ)
- COVID-19 Protein Novavax (COVID-NVX)

### Kombinationsimpfstoffe
- DTPa-IPV-Hib-HepB (6-fach)
- DTPa-IPV (4-fach)
- MMR (Masern-Mumps-Röteln)
- MMRV (MMR + Varizellen)

### Einzelimpfstoffe
- Tetanus-Diphtherie
- Varizellen (Windpocken)
- Hepatitis B

---

## 🎯 Prioritäts-System

| Priorität | Zeitraum | Farbe | Dringlichkeit |
|-----------|----------|-------|---------------|
| **OVERDUE** | Vergangenheit | 🔴 Rot | Höchste |
| **DUE_SOON** | 0-30 Tage | 🟠 Orange | Hoch |
| **UPCOMING** | 31-90 Tage | 🟡 Gelb | Mittel |

---

## 🧪 Demo-Flow (10 Minuten)

### 1. Registrierung & Login (2 Min)
- ✅ Validierungen zeigen (Username, Passwort, E-Mail)
- ✅ Geburtsdatum → Alterskategorie

### 2. Dashboard (2 Min)
- ✅ 4 Statistik-Cards (Overdue, Due Soon, Upcoming, Total)
- ✅ "Ausstehende Impfungen anzeigen"
- ✅ Intelligente Altersberechnung

### 3. Impfungen erfassen (3 Min)
- ✅ COVID-19 hinzufügen (3 Dosen)
- ✅ MMR hinzufügen (historisch)
- ✅ Bearbeiten & Löschen zeigen

### 4. Altersvergleich (3 Min)
- ✅ Säugling: 6-fach, MMR
- ✅ Senior: COVID-19, Tetanus-Diphtherie
- ✅ Unterschiedliche Empfehlungen

---

## 🔧 API-Endpoints (Swagger)

**URL:** http://localhost:8000/swagger-ui/index.html

### Wichtigste Endpoints:

```
Auth:
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/auth/me

Impfungen:
GET  /api/v1/immunization-records/myVaccinations
POST /api/v1/immunization-records
PATCH /api/v1/immunization-records/{id}
DELETE /api/v1/immunization-records/{id}

Impfpläne:
GET  /api/v1/immunization-schedule/pending
GET  /api/v1/immunization-schedule/pending/summary
GET  /api/v1/immunization-schedule/pending/{priority}

Stammdaten:
GET  /api/v1/vaccine-types
GET  /api/v1/age-categories
GET  /api/v1/active-substances
```

---

## 🛡️ Validierungen

| Feld | Regel |
|------|-------|
| Username | 3-50 Zeichen |
| Passwort | Min 6 Zeichen |
| E-Mail | Valides Format |
| Geburtsdatum | Nicht in Zukunft |
| Verabreichungsdatum | Nicht in Zukunft |
| Dosisnummer | Integer ≥ 1 |

---

## 🏗️ Architektur

```
┌─────────────┐
│  Frontend   │ :3000
│   (React)   │
└──────┬──────┘
       │
┌──────▼──────────────┐
│  Auth Service       │ :8000 (öffentlich)
│  (API Gateway)      │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│  Core Backend       │ :8081 (intern)
│  (Spring Boot)      │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│  PostgreSQL         │ :5432 (intern)
│  (Datenbank)        │
└─────────────────────┘
```

---

## 🧬 6-fach Impfstoff (Beispiel)

**DTPa-IPV-Hib-HepB** enthält 6 Wirkstoffe:
1. **D**iphtheria Toxoid
2. **T**etanus Toxoid
3. **P**ertussis (Keuchhusten)
4. **IPV** (Polio)
5. **Hib** (Haemophilus influenzae b)
6. **HepB** (Hepatitis B)

**Impfplan für Säuglinge:**
- Dosis 1: 2 Monate (60 Tage)
- Dosis 2: 4 Monate (120 Tage)
- Dosis 3: 6 Monate (180 Tage)
- Dosis 4: 15-18 Monate (450-540 Tage)

---

## 🎤 Key Talking Points

### 1. **Altersspezifische Intelligenz**
> "Das System berechnet Impfungen in Tagen-Präzision. Ein 3-monatiges Baby bekommt andere Empfehlungen als ein 6-monatiges."

### 2. **Priorisierung**
> "Überfällige Impfungen werden rot markiert, fällige in den nächsten 30 Tagen orange, und zukünftige gelb."

### 3. **Impfstammdaten**
> "Wir haben 12 Impfstofftypen mit 15 Wirkstoffen. Ein 6-fach Impfstoff enthält 6 verschiedene Wirkstoffe."

### 4. **Follow-Up-Rules**
> "Nach Grundimmunisierung als Säugling folgt automatisch die Auffrischung als Vorschulkind – definiert durch Follow-Up-Rules."

### 5. **Microservice-Architektur**
> "API Gateway für Authentifizierung, Core Backend für Business Logic, separate Services für Notifications."

---

## 🔍 Bonus: Datenbank-Einblick

```sql
-- Alle Impfstofftypen anzeigen
SELECT name, code FROM vaccine_type;

-- Alterskategorien
SELECT name, age_min_days, age_max_days FROM age_category;

-- Wirkstoffe eines Impfstoffs
SELECT vt.name, as2.name, vtas.amount
FROM vaccine_type vt
JOIN vaccine_type_active_substance vtas ON vt.id = vtas.vaccine_type_id
JOIN active_substance as2 ON vtas.active_substance_id = as2.id
WHERE vt.code = 'DTPA-IPV-HIB-HEPB';
```

---

## 📝 Cleanup nach Demo

```bash
docker-compose down
docker-compose down -v  # Mit Daten löschen
```

---

## ❓ Häufige Fragen

**Q: Warum Tage statt Jahre?**
**A:** Präzision bei Säuglingen (90 vs. 180 Tage = große Differenz)

**Q: Wie werden Auffrischungen berechnet?**
**A:** Follow-Up-Rules mit Abhängigkeiten (z.B. nach 10 Jahren Tetanus)

**Q: Multi-User?**
**A:** Ja, JWT-basiert, jeder User isoliert

**Q: Admin-Features?**
**A:** Geplant, aktuell nur User-Rolle implementiert

---

**Viel Erfolg! 🎉**
