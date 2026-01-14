# Spring Boosters - Dozenten-Präsentation
## Vollständige Demo-Anleitung

---

## Überblick

**Spring Boosters** ist ein intelligentes Vakzinationsmanagementsystem, das Benutzern hilft, ihre Impfungen zu verwalten und automatisch berechnet, welche Impfungen basierend auf Alter und Impfplänen fällig sind.

### Kernfunktionalität
- Erfassung und Verwaltung persönlicher Impfungen
- Automatische Berechnung ausstehender Impfungen nach Alterskategorien
- Priorisierung: Überfällig, Demnächst fällig, Zukünftig
- Intelligente Altersvalidierung mit 11 Alterskategorien (Säugling bis Senior 70+)

---

## Vorbereitung der Demo

### 1. System starten
```bash
cd /Users/ninoziswiler/Documents/Coding/01_FFHS/FFHS_Projektarbeit_Spring_Boosters
docker-compose up -d
```

### 2. Warten bis alle Services bereit sind (ca. 1-2 Minuten)
```bash
docker-compose ps
```

### 3. Applikation öffnen
- **Frontend**: http://localhost:3000
- **Swagger UI** (API-Dokumentation): http://localhost:8000/swagger-ui/index.html
- **API-Gateway**: http://localhost:8000
- **Core Backend**: http://localhost:8081 (intern)

---

## Demo-Ablauf (ca. 15-20 Minuten)

---

## TEIL 1: Benutzer-Registrierung & Authentifizierung (3 Min)

### Schritt 1.1: Registrierung eines jungen Erwachsenen

**Handlung:**
1. Öffnen Sie http://localhost:3000
2. Klicken Sie auf "Sign Up" Tab
3. Registrieren Sie Benutzer "Anna Müller":
   - **Username**: `anna_mueller`
   - **Vorname**: `Anna`
   - **Nachname**: `Müller`
   - **Geburtsdatum**: `15.05.2000` (23 Jahre alt → "Junge Erwachsene" Kategorie)
   - **E-Mail**: `anna.mueller@example.com`
   - **Passwort**: `secure123`

**Was zu zeigen:**
- ✅ **Validierung**: Alle Felder sind Pflichtfelder
- ✅ **Passwortlänge**: Minimum 6 Zeichen
- ✅ **E-Mail-Format**: Muss valide sein
- ✅ **Username-Länge**: Minimum 3 Zeichen

**Erwartetes Ergebnis:**
- Erfolgreiche Registrierung
- Automatisches Login
- Weiterleitung zum Dashboard

---

### Schritt 1.2: Logout und Re-Login

**Handlung:**
1. Klicken Sie auf "Logout" im Header
2. Loggen Sie sich wieder ein:
   - **Username**: `anna_mueller`
   - **Passwort**: `secure123`

**Was zu zeigen:**
- ✅ **JWT-basierte Authentifizierung**: Token wird in SessionStorage gespeichert
- ✅ **Session Persistence**: Nach Login werden Benutzerdaten geladen

---

## TEIL 2: Dashboard & Impfungsstatistiken (4 Min)

### Schritt 2.1: Dashboard-Übersicht (Noch leer)

**Handlung:**
1. Nach Login sehen Sie das Dashboard

**Was zu zeigen:**
- 📊 **4 Statistik-Cards**:
  - **Überfällig** (Overdue): 0
  - **Demnächst fällig** (Due Soon, ≤30 Tage): 0
  - **Bald fällig** (Upcoming, 31-90 Tage): 0
  - **Gesamt ausstehend** (Total Pending): 0
- 📋 **Impfungstabelle**: Leer (keine Impfungen erfasst)
- 🔔 **"Ausstehende Impfungen anzeigen"** Button

---

### Schritt 2.2: Ausstehende Impfungen anzeigen

**Handlung:**
1. Klicken Sie auf "Ausstehende Impfungen anzeigen"

**Was zu zeigen:**
- ✅ **Intelligente Berechnung**: System berechnet basierend auf:
  - Alter (23 Jahre = 8.395 Tage)
  - Alterskategorie: "Junge Erwachsene (18-29 Jahre)"
  - Impfpläne für diese Alterskategorie
- ✅ **Priorisierte Liste** mit:
  - Impfname
  - Fälligkeitsdatum
  - Priorität (Überfällig / Demnächst / Zukünftig)
  - Erforderliche vs. abgeschlossene Dosen

**Erwartete Impfungen für junge Erwachsene (23 Jahre):**
- COVID-19 Auffrischung (falls Grundimmunisierung fehlt)
- Tetanus-Diphtherie Auffrischung (alle 10 Jahre)
- Eventuell MMR (falls nicht als Kind geimpft)

---

## TEIL 3: Impfungen erfassen (5 Min)

### Schritt 3.1: Erste COVID-19 Impfung hinzufügen

**Handlung:**
1. Klicken Sie auf "Neue Impfung hinzufügen" (+ Button)
2. Wählen Sie:
   - **Impfstofftyp**: `COVID-19 mRNA Pfizer-BioNTech`
   - **Verabreichungsdatum**: `14.01.2024` (heute)
   - **Dosisnummer**: `1`
3. Speichern

**Was zu zeigen:**
- ✅ **Dropdown mit allen Impfstofftypen**: 12 verschiedene Typen
  - COVID-19 Varianten (Moderna, Pfizer, AstraZeneca, J&J, Novavax)
  - Kombinationsimpfstoffe (6-fach, 4-fach, MMR, MMRV)
  - Einzelimpfstoffe (Tetanus-Diphtherie, Varizellen, Hepatitis B)
- ✅ **Datumsauswahl**: Nur vergangene/heutige Daten erlaubt
- ✅ **Dosisnummer**: Integer-Feld für Tracking

**Erwartetes Ergebnis:**
- Impfung erscheint in der Tabelle
- Statistik-Cards aktualisieren sich
- Ausstehende COVID-19 Dosen ändern sich (z.B. 1 von 3 abgeschlossen)

---

### Schritt 3.2: Zweite und dritte COVID-Dosis hinzufügen

**Handlung:**
1. Fügen Sie hinzu:
   - **Impfstofftyp**: `COVID-19 mRNA Pfizer-BioNTech`
   - **Verabreichungsdatum**: `14.02.2024` (Dosis 2)
   - **Dosisnummer**: `2`
2. Fügen Sie hinzu:
   - **Impfstofftyp**: `COVID-19 mRNA Pfizer-BioNTech`
   - **Verabreichungsdatum**: `14.03.2024` (Dosis 3)
   - **Dosisnummer**: `3`

**Was zu zeigen:**
- ✅ **Serie-Tracking**: System erkennt, dass Grundimmunisierung komplett ist
- ✅ **Automatische Neuberechnung**: Ausstehende Impfungen werden aktualisiert

---

### Schritt 3.3: MMR Impfung hinzufügen

**Handlung:**
1. Fügen Sie hinzu:
   - **Impfstofftyp**: `MMR (Masern-Mumps-Röteln)`
   - **Verabreichungsdatum**: `01.06.2001` (als Kleinkind)
   - **Dosisnummer**: `1`

**Was zu zeigen:**
- ✅ **Historische Daten**: Impfungen aus der Kindheit können erfasst werden
- ✅ **Datum in der Vergangenheit**: Validierung erlaubt alte Daten

---

### Schritt 3.4: Impfung bearbeiten

**Handlung:**
1. Klicken Sie auf "Bearbeiten" (Stift-Icon) bei der MMR Impfung
2. Ändern Sie das Datum auf `15.07.2001`
3. Speichern

**Was zu zeigen:**
- ✅ **Edit-Funktionalität**: Korrekturen möglich
- ✅ **Inline-Bearbeitung**: Direktes Update

---

### Schritt 3.5: Impfung löschen (und abbrechen)

**Handlung:**
1. Klicken Sie auf "Löschen" (Papierkorb-Icon) bei einer Impfung
2. Im Bestätigungsdialog: Klicken Sie "Abbrechen"

**Was zu zeigen:**
- ✅ **Sicherheitsmechanismus**: Bestätigungsdialog verhindert versehentliches Löschen
- ✅ **Abbruch-Option**: Keine Änderung bei Abbruch

---

## TEIL 4: Altersvalidierung demonstrieren (6 Min)

### Schritt 4.1: Registrierung eines Säuglings

**Handlung:**
1. Logout
2. Registrieren Sie neuen Benutzer "Baby Schmidt":
   - **Username**: `baby_schmidt`
   - **Vorname**: `Max`
   - **Nachname**: `Schmidt`
   - **Geburtsdatum**: `14.07.2023` (6 Monate alt)
   - **E-Mail**: `baby.schmidt@example.com`
   - **Passwort**: `secure123`

**Was zu zeigen:**
- ✅ **Alterskategorie "Säuglinge (2-11 Monate)"**
  - Altersbereich: 60-335 Tage
  - Aktuelles Alter: ~182 Tage (6 Monate)

---

### Schritt 4.2: Ausstehende Impfungen für Säugling

**Handlung:**
1. Klicken Sie auf "Ausstehende Impfungen anzeigen"

**Was zu zeigen:**
- ✅ **Säuglings-spezifische Impfungen**:
  - **6-fach Impfung (DTPa-IPV-Hib-HepB)**: 3-4 Dosen erforderlich
    - Wirkstoffe: Diphtherie, Tetanus, Pertussis, Polio, Hib, Hepatitis B
  - **MMR**: Erste Dosis mit 12 Monaten
  - **Varizellen**: Je nach Plan

**Erwartetes Verhalten:**
- System zeigt NUR Impfungen, die für Alterskategorie "Säuglinge" relevant sind
- Keine Erwachsenen-Impfungen (z.B. COVID-19 Auffrischung)

---

### Schritt 4.3: Registrierung eines Seniors

**Handlung:**
1. Logout
2. Registrieren Sie "Senior Werner":
   - **Username**: `senior_werner`
   - **Vorname**: `Klaus`
   - **Nachname**: `Werner`
   - **Geburtsdatum**: `15.03.1948` (75 Jahre alt)
   - **E-Mail**: `klaus.werner@example.com`
   - **Passwort**: `secure123`

**Was zu zeigen:**
- ✅ **Alterskategorie "Senioren (70+ Jahre)"**
  - Altersbereich: 25.550+ Tage (offen)
  - Aktuelles Alter: ~27.740 Tage (75 Jahre)

---

### Schritt 4.4: Ausstehende Impfungen für Senioren

**Handlung:**
1. Klicken Sie auf "Ausstehende Impfungen anzeigen"

**Was zu zeigen:**
- ✅ **Senioren-spezifische Impfungen**:
  - **COVID-19**: Regelmäßige Auffrischung empfohlen
  - **Tetanus-Diphtherie**: Auffrischung alle 10 Jahre
  - **Influenza** (falls implementiert): Jährlich
  - **Pneumokokken** (falls implementiert): Einmalig ab 65

**Besonderheit:**
- Keine Kinder-Impfungen (MMR, 6-fach, etc.)
- Fokus auf Auffrischungen und Alters-spezifische Impfungen

---

### Schritt 4.5: Vergleich der Alterskategorien (Zusammenfassung)

**Zeigen Sie in einer Tabelle:**

| Alterskategorie | Altersbereich | Typische Impfungen |
|-----------------|---------------|-------------------|
| Säuglinge (2-11 Monate) | 60-335 Tage | 6-fach (4 Dosen), MMR (erste Dosis) |
| Kleinkinder (1-2 Jahre) | 365-730 Tage | MMR, Varizellen, 6-fach Auffrischung |
| Vorschulkinder (3-5 Jahre) | 1.095-1.825 Tage | MMRV, 4-fach Auffrischung |
| Schulkinder (6-11 Jahre) | 2.190-4.015 Tage | Auffrischungen |
| Jugendliche (12-17 Jahre) | 4.380-6.570 Tage | HPV (falls implementiert), Auffrischungen |
| Junge Erwachsene (18-29 Jahre) | 6.570-10.585 Tage | COVID-19, Tetanus-Diphtherie (alle 10 Jahre) |
| Erwachsene (30-39 Jahre) | 10.950-14.235 Tage | COVID-19, Tetanus-Diphtherie |
| Erwachsene (40-49 Jahre) | 14.600-17.885 Tage | COVID-19, Tetanus-Diphtherie |
| Erwachsene (50-59 Jahre) | 18.250-21.535 Tage | COVID-19, Tetanus-Diphtherie |
| Erwachsene (60-69 Jahre) | 21.900-25.185 Tage | COVID-19, Tetanus-Diphtherie, ggf. Pneumokokken |
| Senioren (70+ Jahre) | 25.550+ Tage | COVID-19, Tetanus-Diphtherie, Influenza, Pneumokokken |

**Was zu betonen:**
- ✅ **Altersspezifische Filterung**: System zeigt nur relevante Impfungen
- ✅ **Dynamische Berechnung**: Basierend auf aktuellem Alter in Tagen
- ✅ **11 Granulare Kategorien**: Von Säugling bis Senior 70+

---

## TEIL 5: API-Dokumentation & Swagger UI (4 Min)

### Schritt 5.1: Swagger UI öffnen

**Handlung:**
1. Öffnen Sie http://localhost:8000/swagger-ui/index.html

**Was zu zeigen:**
- ✅ **Vollständige REST API-Dokumentation**
- ✅ **Gruppierung nach Controllern**:
  - `user-controller` (Auth)
  - `immunization-record-controller`
  - `immunization-schedule-controller`
  - `vaccine-type-controller`
  - `age-category-controller`
  - `active-substance-controller`
  - `immunization-plan-controller`

---

### Schritt 5.2: API-Endpoint testen (GET /vaccine-types)

**Handlung:**
1. Öffnen Sie `vaccine-type-controller`
2. Klicken Sie auf `GET /api/v1/vaccine-types`
3. Klicken Sie "Try it out"
4. Klicken Sie "Execute"

**Was zu zeigen:**
- ✅ **Response 200**: Liste aller Impfstofftypen
- ✅ **12 Impfstofftypen** mit Details:
  - ID (UUID)
  - Name (z.B. "COVID-19 mRNA Moderna")
  - Code (z.B. "COVID-MOD")
  - Created-Timestamp

**Beispiel-Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "COVID-19 mRNA Moderna",
    "code": "COVID-MOD",
    "createdAt": "2024-01-01T10:00:00Z"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "COVID-19 mRNA Pfizer-BioNTech",
    "code": "COVID-PFZ",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

---

### Schritt 5.3: API-Endpoint testen (GET /age-categories)

**Handlung:**
1. Öffnen Sie `age-category-controller`
2. Klicken Sie auf `GET /api/v1/age-categories`
3. Klicken Sie "Try it out" → "Execute"

**Was zu zeigen:**
- ✅ **11 Alterskategorien** mit:
  - ID (UUID)
  - Name (z.B. "Säuglinge (2-11 Monate)")
  - Altersbereich in Tagen:
    - `ageMinDays`: 60
    - `ageMaxDays`: 335 (null für offene Kategorie "70+ Jahre")

**Beispiel-Response:**
```json
[
  {
    "id": "uuid-1",
    "name": "Säuglinge (2-11 Monate)",
    "ageMinDays": 60,
    "ageMaxDays": 335
  },
  {
    "id": "uuid-11",
    "name": "Senioren (70+ Jahre)",
    "ageMinDays": 25550,
    "ageMaxDays": null
  }
]
```

---

### Schritt 5.4: Protected Endpoint testen (mit JWT)

**Handlung:**
1. Öffnen Sie `immunization-record-controller`
2. Klicken Sie auf `GET /api/v1/immunization-records/myVaccinations`
3. Klicken Sie "Try it out"
4. Fügen Sie im Header "Authorization" den JWT-Token ein:
   - Format: `Bearer <token>`
   - Token aus dem Frontend SessionStorage holen (Developer Tools → Application → Session Storage)
5. Klicken Sie "Execute"

**Was zu zeigen:**
- ✅ **JWT-basierte Authentifizierung**: Ohne Token → 401 Unauthorized
- ✅ **Mit Token**: Liste aller Impfungen des eingeloggten Users
- ✅ **User-Isolation**: Jeder User sieht nur eigene Daten

---

## TEIL 6: Impfstammdaten & Wirkstoffe (3 Min)

### Schritt 6.1: Wirkstoffe anzeigen

**Handlung:**
1. In Swagger UI: `GET /api/v1/active-substances`
2. Execute

**Was zu zeigen:**
- ✅ **15 Wirkstoffe** definiert:

| Wirkstoff-Name | Synonyme | Verwendung |
|----------------|----------|-----------|
| mRNA-1273 | Moderna mRNA, Spikevax | COVID-19 Moderna |
| BNT162b2 | Pfizer mRNA, Comirnaty | COVID-19 Pfizer |
| ChAdOx1-S | Vaxzevria | COVID-19 AstraZeneca |
| Tetanus Toxoid | Tetanus-Impfstoff | 6-fach, 4-fach, Td |
| Diphtheria Toxoid | Diphtherie-Impfstoff | 6-fach, 4-fach, Td |
| Pertussis | Keuchhusten-Impfstoff | 6-fach, 4-fach |
| Polio | Poliomyelitis-Impfstoff | 6-fach, 4-fach |
| Haemophilus influenzae b | Hib-Impfstoff | 6-fach |
| Hepatitis B | HBV-Impfstoff | 6-fach, Hepatitis B |
| Measles | Masern-Impfstoff | MMR, MMRV |
| Mumps | Mumps-Impfstoff | MMR, MMRV |
| Rubella | Röteln-Impfstoff | MMR, MMRV |
| Varicella | Windpocken-Impfstoff | MMRV, Varizellen |

---

### Schritt 6.2: Impfstofftyp zu Wirkstoff-Zuordnung

**Was zu zeigen (konzeptionell):**

**6-fach Impfstoff (DTPa-IPV-Hib-HepB)** enthält:
1. Diphtheria Toxoid
2. Tetanus Toxoid
3. Pertussis
4. Polio
5. Haemophilus influenzae b (Hib)
6. Hepatitis B

**MMR (Masern-Mumps-Röteln)** enthält:
1. Measles
2. Mumps
3. Rubella

**COVID-19 mRNA Moderna** enthält:
1. mRNA-1273

**Was zu betonen:**
- ✅ **Many-to-Many Beziehung**: Ein Impfstofftyp kann mehrere Wirkstoffe enthalten
- ✅ **Dosierung**: Junction-Tabelle speichert Menge pro Wirkstoff
- ✅ **Synonyme**: PostgreSQL ARRAY für alternative Namen

---

## TEIL 7: Impfpläne & Follow-Up-Rules (2 Min)

### Schritt 7.1: Impfpläne anzeigen

**Handlung:**
1. In Swagger UI: `GET /api/v1/immunization-plans`
2. Execute

**Was zu zeigen:**
- ✅ **Impfpläne** sind Kombinationen von:
  - Impfstofftyp (z.B. COVID-19 Pfizer)
  - Alterskategorie (z.B. Junge Erwachsene)
- ✅ **Serien innerhalb eines Plans**:
  - Serie-Name (z.B. "Grundimmunisierung", "Auffrischung")
  - Erforderliche Dosen (z.B. 3 Dosen für COVID-19)

**Beispiel:**
```
Impfplan: "COVID-19 Pfizer für Junge Erwachsene (18-29 Jahre)"
├─ Serie 1: "Grundimmunisierung"
│  └─ Erforderliche Dosen: 2
└─ Serie 2: "Auffrischung"
   └─ Erforderliche Dosen: 1
```

---

### Schritt 7.2: Follow-Up-Rules (konzeptionell)

**Was zu erklären:**

**Follow-Up-Rules** definieren Abhängigkeiten zwischen Impfplänen:

**Beispiel:**
```
Von: "6-fach Grundimmunisierung (Säuglinge)"
Nach: "4-fach Auffrischung (Vorschulkinder)"
Bedingungen:
  - Mindestens 4 Dosen der Grundimmunisierung abgeschlossen
  - Ziel-Alter: 4-6 Jahre (1.460-2.190 Tage)
  - Mindestabstand seit letzter Dosis: 365 Tage
```

**Was zu betonen:**
- ✅ **Automatische Berechnung**: System empfiehlt Follow-Up basierend auf Rules
- ✅ **Alters- und Dosierungsabhängig**: Berücksichtigt beide Faktoren
- ✅ **Zeitabstände**: Min-Intervalle zwischen Dosen

---

## TEIL 8: Prioritäts-System demonstrieren (3 Min)

### Schritt 8.1: Zurück zu Anna Müller

**Handlung:**
1. Logout aus Senior-Account
2. Login als `anna_mueller`

---

### Schritt 8.2: Prioritäten erklären

**Handlung:**
1. Klicken Sie auf "Ausstehende Impfungen anzeigen"

**Was zu zeigen:**

**Prioritäts-Kategorien:**

1. **OVERDUE (Überfällig)** - ROT
   - Fälligkeitsdatum liegt in der Vergangenheit
   - Beispiel: Tetanus-Auffrischung war vor 2 Monaten fällig
   - Dringendste Kategorie

2. **DUE_SOON (Demnächst fällig)** - ORANGE
   - Fälligkeitsdatum in den nächsten 0-30 Tagen
   - Beispiel: COVID-19 Auffrischung in 15 Tagen
   - Hohe Priorität

3. **UPCOMING (Bald fällig)** - GELB
   - Fälligkeitsdatum in 31-90 Tagen
   - Beispiel: Hepatitis B in 60 Tagen
   - Mittlere Priorität

**Statistik-Cards zeigen:**
- Anzahl pro Kategorie
- Total ausstehend (Summe aller 3 Kategorien)

---

### Schritt 8.3: Manuelle Filterung nach Priorität

**Handlung:**
1. In Swagger UI: `GET /api/v1/immunization-schedule/pending/{priority}`
2. Path-Parameter: `overdue`
3. Authorization-Header mit Anna's Token
4. Execute

**Was zu zeigen:**
- ✅ **Gefilterte Liste**: Nur überfällige Impfungen
- ✅ **API-Flexibilität**: Frontend kann nach Priorität filtern
- ✅ **Sortierung**: Überfällige zuerst (nach Dringlichkeit)

---

## TEIL 9: Mehrsprachigkeit (1 Min)

### Schritt 9.1: Sprachenwechsel

**Handlung:**
1. Im Frontend-Header: Klicken Sie auf Sprachauswahl
2. Wechseln Sie zwischen Deutsch und Englisch

**Was zu zeigen:**
- ✅ **i18n-Unterstützung**: Alle UI-Texte werden übersetzt
- ✅ **Dynamischer Wechsel**: Ohne Seitenreload
- ✅ **Persistenz**: Sprachauswahl bleibt erhalten (LocalStorage)

---

## TEIL 10: Validierungen zusammenfassen (2 Min)

### Validierungen im Überblick:

| Validierung | Regel | Fehlermeldung |
|-------------|-------|--------------|
| **Username** | Min 3, Max 50 Zeichen | "Username muss zwischen 3 und 50 Zeichen sein" |
| **Passwort** | Min 6 Zeichen | "Passwort muss mindestens 6 Zeichen lang sein" |
| **E-Mail** | Valides Format | "Ungültige E-Mail-Adresse" |
| **Geburtsdatum** | Nicht in der Zukunft | "Geburtsdatum darf nicht in der Zukunft liegen" |
| **Verabreichungsdatum** | Nicht in der Zukunft | "Impfung kann nicht in der Zukunft liegen" |
| **Dosisnummer** | Integer ≥ 1 | "Dosisnummer muss mindestens 1 sein" |
| **Alterskategorie** | Aktuelles Alter in Range | Automatische Filterung |
| **Impfplan-Serie** | Anzahl Dosen ≤ requiredDoses | Tracking im System |

---

## TEIL 11: Architektur zeigen (Optional, 2 Min)

### Schritt 11.1: Docker-Container zeigen

**Handlung:**
```bash
docker-compose ps
```

**Was zu zeigen:**
- ✅ **5 Container**:
  1. `postgres` - Datenbank (PostgreSQL 17)
  2. `corebackend` - Spring Boot Backend (Port 8081, intern)
  3. `notification-service` - Notification Service (Port 8082, intern)
  4. `authentification_service` - API Gateway (Port 8000, öffentlich)
  5. `frontend` - React Frontend (Port 3000, öffentlich)

---

### Schritt 11.2: Logs zeigen (optional)

**Handlung:**
```bash
docker-compose logs corebackend | tail -20
```

**Was zu zeigen:**
- ✅ **Spring Boot Startup-Logs**
- ✅ **Flyway Migrationen**: Datenbank-Schema erstellt
- ✅ **JPA Hibernate**: Schema-Validierung erfolgreich

---

## Zusammenfassung für Q&A

### Kernpunkte hervorheben:

1. **Intelligente Altersvalidierung**
   - 11 granulare Alterskategorien
   - Automatische Berechnung in Tagen
   - Dynamische Filterung relevanter Impfungen

2. **Umfassende Impfstammdaten**
   - 12 Impfstofftypen
   - 15 Wirkstoffe
   - Many-to-Many Beziehungen

3. **Priorisierungssystem**
   - Überfällig / Demnächst / Bald
   - 0-30 Tage / 31-90 Tage
   - Statistik-Dashboard

4. **RESTful API**
   - JWT-Authentifizierung
   - Swagger/OpenAPI Dokumentation
   - User-isolierte Daten

5. **Microservice-Architektur**
   - API Gateway (Authentification Service)
   - Core Backend (Business Logic)
   - Notification Service
   - PostgreSQL Datenbank
   - React Frontend

6. **Follow-Up-Rules**
   - Automatische Berechnung von Auffrischungen
   - Alters- und dosisabhängig
   - Zeitabstände berücksichtigt

---

## Häufige Fragen & Antworten

**Q: Warum wird das Alter in Tagen gespeichert?**
**A:** Für präzise Berechnung bei Säuglingen/Kleinkindern. Ein 3 Monate altes Baby (~90 Tage) hat andere Impfungen als ein 6 Monate altes (~180 Tage).

**Q: Wie werden neue Impfstofftypen hinzugefügt?**
**A:** Über Admin-Endpoints (POST /api/v1/vaccine-types). Aktuell nur manuell, zukünftig über Admin-UI geplant.

**Q: Unterstützt das System mehrere Benutzer?**
**A:** Ja, jeder Benutzer hat isolierte Daten. JWT-Token enthält User-ID für Zugriffskontrolle.

**Q: Wie werden Auffrischungen berechnet?**
**A:** Via Follow-Up-Rules, die Abhängigkeiten zwischen Impfplänen definieren (z.B. nach 10 Jahren Tetanus-Auffrischung).

**Q: Kann man Impfungen für Kinder/Familienmitglieder verwalten?**
**A:** Aktuell: Nein (nur eigener Account). Zukünftig: Familienaccounts geplant.

**Q: Sind die Impfpläne länderspezifisch?**
**A:** Aktuell basierend auf Schweizer/deutschen Empfehlungen. System ist erweiterbar für andere Länder.

---

## Cleanup nach Demo

```bash
# Container stoppen
docker-compose down

# Datenbank-Volumes löschen (optional, für Fresh Start)
docker-compose down -v

# Container und Images löschen (optional)
docker-compose down --rmi all -v
```

---

**Viel Erfolg bei der Präsentation!**
