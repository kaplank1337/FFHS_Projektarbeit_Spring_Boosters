# Core Backend – Vaccination Management Service

Das Modul `core_backend` ist der zentrale Fach‑Service der Spring‑Boosters‑Anwendung. Hier liegen das komplette Datenmodell und die Business‑Logik rund um Benutzer, Impfstoffe, Impfpläne und tatsächlich verabreichte Impfungen.

Über dieses Modul können Benutzer Impfungen erfassen und abrufen. Darauf basierend berechnet der Service, welche Impfungen fällig, bald fällig oder überfällig sind.

## Überblick

- **Technologie:**
  - Spring Boot 3 (Web, Validation, Data JPA)
  - Java 21
  - PostgreSQL mit Flyway‑Migrationen
  - JWT für Authentifizierung (Token wird vom Authentication‑Service ausgewertet)
  - OpenAPI/Swagger für API‑Dokumentation
- **Architektur:** klassische Layered Architecture
  - Controller (API‑Layer)
  - Service (Business‑Logik)
  - Repository (Datenzugriff)
  - Entity/DTO/Mapper (Datenmodell + Transport)

---

## Datenmodell

Vereinfacht besteht das Domänenmodell aus diesen Hauptentitäten (in `ch.ffhs.spring_boosters.controller.entity`):

### `User`

Repräsentiert einen Benutzer des Systems.

Wichtige Felder:
- `id` (UUID): Primärschlüssel.
- `username` (unique): Benutzername.
- `passwordHash`: Passwort‑Hash.
- `firstName`, `lastName`: Vor‑ und Nachname.
- `birthDate` (LocalDate): Geburtsdatum.
- `email` (unique): eindeutige E‑Mail‑Adresse.
- `role`: Rolle im System (z. B. `USER`, `ADMIN`).
- `createdAt`, `updatedAt`: Audit‑Timestamps.

Besondere Logik:
- `getAgeYears()`: transientes Feld, das aus dem Geburtsdatum das Alter in Jahren berechnet. Wird nicht persistiert, sondern nur für Responses berechnet.
- Beziehung: `@OneToMany` zu `ImmunizationRecord` – ein Benutzer kann viele Impfungen haben.

### `VaccineType`

Beschreibt einen Impfstofftyp (z. B. „MMR“, „Tetanus“).

Typische Felder:
- `id` (UUID)
- `name` (z. B. „MMR (Masern-Mumps-Röteln)“)
- `code` (z. B. ein offizieller Impfcode)

Beziehungen:
- `@OneToMany` zu `ImmunizationPlan` (welche Impfpläne gibt es für diesen Impfstoff?)
- `@OneToMany` zu `ImmunizationRecord` (welche realen Impfungen wurden mit diesem Typ durchgeführt?)
- Beziehung zu `ActiveSubstance` über `VaccineTypeActiveSubstance` (Join‑Tabelle)

### `ActiveSubstance`

Repräsentiert einen Wirkstoff in einem Impfstoff.

### `AgeCategory`

Definiert Alterskategorien in Tagen (z. B. „Säugling“, „Kind“, „Erwachsener“).

Typische Felder:
- `id` (UUID)
- `name` (z. B. „0–12 Monate“)
- `ageMinDays`, `ageMaxDays`: Alter in Tagen, für das diese Kategorie gilt.

Diese Kategorien werden genutzt, um Impfpläne an ein bestimmtes Lebensalter zu koppeln.

### `ImmunizationPlan`

Beschreibt einen empfohlenen Impfplan für einen Impfstoff in einer bestimmten Alterskategorie.

Wichtige Felder:
- `id` (UUID)
- `name`: Name des Plans (z. B. „Grundimmunisierung Säugling“).
- `vaccineTypeId`: Referenz auf `VaccineType`.
- `ageCategoryId`: Referenz auf `AgeCategory`.
- `immunizationPlanSeries`: Liste von Serien‑Einträgen, die angeben, wie viele Dosen (und ggf. in welchem Abstand) empfohlen werden.

Beziehungen:
- `@ManyToOne` `vaccineType` und `ageCategory`.
- `@OneToMany` `immunizationRecords`: tatsächlich erfasste Impfungen, die zu diesem Plan gehören.
- `FollowUpRule`: Regeln, welche Folgeimpfung einem Plan nachgelagert ist.

### `ImmunizationPlanSeries`

Beschreibt Serien innerhalb eines Plans, z. B. „3 Dosen im Abstand von X Tagen“.

Wichtige Felder:
- `requiredDoses`: wie viele Dosen sind für diese Serie erforderlich?

### `FollowUpRule`

Definiert Abhängigkeiten zwischen Impfplänen (z. B. Auffrischungsimpfung nach Grundimmunisierung).

Felder (vereinfacht):
- `fromPlan` → `toPlan`: welcher Plan folgt auf welchen?
- ggf. Mindestabstände etc.

### `ImmunizationRecord`

Repräsentiert eine tatsächlich verabreichte Impfung eines Benutzers.

Wichtige Felder:
- `id` (UUID)
- `userId`: Referenz auf den geimpften Benutzer.
- `vaccineTypeId`: verwendeter Impfstofftyp.
- `immunizationPlanId`: zugehöriger Impfplan.
- `administeredOn`: Datum, an dem die Impfung verabreicht wurde.
- `doseOrderClaimed`: welche Dosisnummer innerhalb der Serie (1., 2., 3. …).
- `createdAt`, `updatedAt`: Audit.

Beziehungen:
- `@ManyToOne` zu `User`, `VaccineType`, `ImmunizationPlan` (bidirektional zu den oben beschriebenen Entitäten).

---

## Kern‑Funktion: Fällige Impfungen ermitteln

Diese Logik steckt im Service `ImmunizationScheduleServiceImpl`.

Vereinfacht passiert Folgendes:

1. Benutzer anhand seiner `userId` laden.
2. Aus `birthDate` das aktuelle Alter in Tagen berechnen.
3. Alle bestehenden `ImmunizationRecord` des Benutzers laden.
4. Für jeden `ImmunizationPlan` prüfen:
   - Passt die Alterskategorie zum aktuellen Alter?
   - Wie viele Dosen sind laut Plan erforderlich?
   - Wie viele Dosen sind bereits als `ImmunizationRecord` erfasst?
5. Wenn noch Dosen fehlen, wird eine `PendingImmunizationDto` erzeugt.
6. Für jede offene Impfung wird eine Priorität bzw. ein Status berechnet (z. B. „overdue“, „due-soon“, „upcoming“), abhängig vom Alter, den empfohlenen Abständen und vorhandenen Dosen.
7. Das Ergebnis wird im `ImmunizationScheduleDto` zusammengefasst:
   - `totalPending`
   - Anzahl `overdue`, `due-soon`, `upcoming`
   - Liste aller offenen Impfungen mit Detailinfos.

Damit kann das Frontend dem Benutzer anzeigen, welche Impfungen dringend, bald oder später fällig sind.

---

## Architektur – Layered Architecture

Das Modul folgt einer klassischen Schichtenarchitektur:

### 1. Controller Layer (`ch.ffhs.spring_boosters.controller`)

- Klassen wie `UserController`, `ImmunizationRecordController`, `ImmunizationPlanController`, `ImmunizationScheduleController`, `VaccineTypeController`, `AgeCategoryController`, `ActiveSubstanceController`.
- Verantwortlich für:
  - HTTP‑Endpunkte (`@RestController`)
  - Request‑Parsing und Response‑Aufbau
  - Validierung der Eingabedaten mit Jakarta Validation (`@Valid`)
  - Rückgabe von DTOs statt Entities
- Ruft ausschließlich Services auf, enthält keine Business‑Logik.

### 2. DTOs & Mapper (`controller.dto`, `controller.mapper`)

- DTOs repräsentieren Daten, die nach außen über die API gehen oder von Clients kommen.
- Mapper wie `UserMapper`, `ImmunizationRecordMapper`, `ImmunizationPlanMapper` konvertieren zwischen Entity und DTO.
- Dadurch bleibt das Domänenmodell von der API‑Repräsentation entkoppelt.

### 3. Service Layer (`ch.ffhs.spring_boosters.service` + `service.implementation`)

- Interfaces, z. B. `UserService`, `ImmunizationRecordService`, `ImmunizationPlanService`, `ImmunizationScheduleService`.
- Implementierungen, z. B. `UserServiceImpl`, `ImmunizationRecordServiceImpl`, `ImmunizationScheduleServiceImpl`.
- Verantwortlich für:
  - Geschäftslogik (z. B. Validierungsregeln, Berechnung von Fälligkeiten)
  - Orchestrierung mehrerer Repositories
  - Umgang mit Domain‑Exceptions (z. B. `UserNotFoundException`, `ImmunizationPlanNotFoundException`).

### 4. Repository Layer (`ch.ffhs.spring_boosters.repository`)

- Spring Data JPA Repositories wie `UserRepository`, `ImmunizationRecordRepository`, `ImmunizationPlanRepository`, `AgeCategoryRepository`, `VaccineTypeRepository`, `ActiveSubstanceRepository`.
- Verantwortlich für Datenzugriffe (CRUD, Queries).
- Einfache Schnittstellen gegen die Entitäten.

### 5. Persistence / Entity Layer (`controller.entity`)

- Enthält die JPA Entities und die Beziehungsdefinitionen.
- Abbildung direkt auf das PostgreSQL‑Schema `spring_boosters`.

### 6. Cross‑Cutting

- `config.JwtTokenReader`: liest JWT‑Daten aus dem vom Gateway weitergereichten Token.
- `security.JwtService`: kapselt JWT‑Operationen im Backend.
- `controller.exception.GlobalExceptionHandler`: globaler Exception‑Handler, der Fach‑Exceptions in saubere HTTP‑Responses umwandelt.
- `OpenApiConfig`: Konfiguration der Swagger‑/OpenAPI‑Dokumentation.

---

## Persistenz & Migration

### Datenbank

- DB: PostgreSQL
- Default‑Schema: `spring_boosters` (konfiguriert in `application.yaml`).

Ausschnitt aus `application.yaml`:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://postgres:5432/spring_booster_db}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:validate}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration
    default-schema: spring_boosters
```

- `ddl-auto: validate` sorgt dafür, dass das Schema von Flyway bereitgestellt werden muss – Hibernate erzeugt keine Tabellen selbst.
- Flyway‑Skripte liegen unter `src/main/resources/db/migration` und bauen Schema und Basisdaten auf (Tabellen für User, AgeCategories, VaccineTypes, ImmunizationPlans, etc.).

---

## Build & Start (ohne Docker)

### Voraussetzungen

- Java 21
- Maven 3.9+
- Laufende PostgreSQL‑Datenbank mit den Parametern aus `application.yaml` oder passenden Umgebungsvariablen.

### Anwendung starten

Im Modul‑Verzeichnis `core_backend`:

```bash
./mvnw spring-boot:run
```

Standard‑Port: `8081` (konfigurierbar über `SERVER_PORT`).

Die REST‑API ist dann erreichbar unter `http://localhost:8081`, typischerweise aber nur über das API‑Gateway (`authentification_service`), das auf `http://localhost:8000` lauscht.

Swagger‑UI:

- `http://localhost:8081/swagger-ui.html`

---

## Docker

Für den Docker‑Betrieb gibt es zwei Dockerfiles:

### 1. `core_backend/Dockerfile` – Backend‑Service

Zweiphasen‑Build:

1. **Build Stage**
   - Basis: `maven:3.9.9-eclipse-temurin-21`
   - Lädt Dependencies und baut ein JAR (`core_backend-0.0.1-SNAPSHOT.jar`).
2. **Run Stage**
   - Basis: `eclipse-temurin:21-jre`
   - Kopiert das JAR und startet es auf Port `8081`.

Beispiel Build & Run lokal:

```bash
# Image bauen
cd core_backend
docker build -t core_backend .

# Container starten (DB muss erreichbar sein)
docker run \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/spring_booster_db" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  core_backend
```

### 2. `core_backend/docker/Dockerfile` – PostgreSQL für lokale/Compose‑Setups

Dieses Dockerfile startet eine Postgres‑Instanz mit vordefinierten Credentials:

```dockerfile
FROM postgres:17-alpine
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=spring_booster_db
EXPOSE 5432
```

Üblicherweise wird in `docker-compose.yml`:

- ein DB‑Service aus diesem Image gestartet,
- der `core_backend`‑Container im selben Netzwerk betrieben,
- Port `8081` wird nur für das API‑Gateway sichtbar gemacht (nicht direkt ins Internet gemappt).

---

## Zusammenspiel mit dem Authentication‑Service

In der Gesamtarchitektur dient das Modul `authentification_service` als API‑Gateway und einziger öffentlich sichtbarer Einstiegspunkt.

- Externe Clients sprechen `http://localhost:8000` an.
- Das Gateway validiert das JWT und leitet Anfragen an `core_backend` weiter (Service‑Adresse z. B. `http://corebackend:8081` im Docker‑Netzwerk).
- `core_backend` konzentriert sich ausschließlich auf die Fachlogik und geht von einem bereits authentifizierten Benutzer aus (Benutzerdaten werden aus dem JWT ausgelesen).

Dadurch entsteht eine klare Trennung:

- **Authentication‑/Gateway‑Schicht:** Security, Routing, globale Policies.
- **Core Backend:** Domänenspezifische Logik und Datenmodell rund um Impfungen.

---

## Tests

Unter `src/test/java/ch/ffhs/spring_boosters` befinden sich umfangreiche Tests:

- Unit‑Tests für Mapper, Services, Repositories
- Controller‑Tests
- Integrationstests, die über HTTP‑Requests das Zusammenspiel von Controller, Service, Repository und DB prüfen.

Ausführen:

```bash
cd core_backend
./mvnw test
```

Diese Tests helfen dabei, möglichst viele Code‑Pfade („jeden Zentimeter“) abzudecken: vom REST‑Endpoint über die Business‑Logik bis in die Datenbank.

