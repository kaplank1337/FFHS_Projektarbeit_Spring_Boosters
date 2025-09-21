# Spring Boosters - Immunization Management System

## 📋 Projektbeschreibung

Das Spring Boosters Projekt ist ein Immunization Management System, das entwickelt wurde, um Impfpläne zu verwalten und Impfungen nachzuverfolgen. Das System bietet eine REST-API für die Verwaltung von Benutzern, Impfstoffen und Impfplänen.

## 🚀 Quick Start

### Voraussetzungen

- Java 21
- Maven 3.6+
- Docker
- PostgreSQL (läuft über Docker)

### 🐳 Datenbank Setup

Die Anwendung verwendet PostgreSQL als Datenbank, die über Docker bereitgestellt wird.

#### Container bauen und starten

> ⚠️ **Wichtig**: Die Datenbank läuft auf Port **5434**, da Port 5432 bereits durch Docker Desktop belegt sein könnte.

```bash
# 1. Navigieren Sie zum Docker-Verzeichnis
cd docker

# 2. Docker Image bauen
docker build -t spring-boosters-db .

# 3. Container starten
docker run -d -p 5434:5432 --name spring-booster-db spring-boosters-db
```

#### Verbindungsdetails
- **Host**: localhost
- **Port**: 5434
- **Database**: spring_booster_db
- **Username**: postgres
- **Password**: postgres

### 🏃‍♂️ Anwendung starten

```bash
# Maven Dependencies installieren
mvn clean install

# Anwendung starten
mvn spring-boot:run
```

Die Anwendung ist dann verfügbar unter: http://localhost:8080

## 📚 API-Dokumentation (Swagger UI)

Das System bietet eine vollständige interaktive API-Dokumentation über Swagger UI basierend auf der OpenAPI 3.0 Spezifikation.

### 🌐 Zugriff auf die API-Dokumentation

Nach dem Start der Anwendung sind folgende URLs verfügbar:

#### Swagger UI (Interaktive Dokumentation)
```
http://localhost:8080/swagger-ui.html
```

#### OpenAPI 3.0 JSON Specification
```
http://localhost:8080/v3/api-docs
```

### 🔑 API-Authentifizierung über Swagger UI

Da alle API-Endpoints (außer Registrierung und Login) JWT-Authentifizierung erfordern, müssen Sie sich zunächst authentifizieren:

#### Schritt 1: Benutzer registrieren oder anmelden

1. **Öffnen Sie Swagger UI**: http://localhost:8080/swagger-ui.html
2. **Navigieren Sie zur "Benutzer" Sektion**
3. **Registrierung** (falls noch kein Account vorhanden):
   - Klicken Sie auf `POST /api/v1/users/register`
   - Klicken Sie auf "Try it out"
   - Füllen Sie die Benutzerdaten aus:
   ```json
   {
     "username": "testuser",
     "password": "password123",
     "firstName": "Max",
     "lastName": "Mustermann",
     "birthDate": "1990-01-01"
   }
   ```
   - Klicken Sie auf "Execute"

4. **Anmeldung**:
   - Klicken Sie auf `POST /api/v1/users/login`
   - Klicken Sie auf "Try it out"
   - Geben Sie Ihre Anmeldedaten ein:
   ```json
   {
     "username": "testuser",
     "password": "password123"
   }
   ```
   - Klicken Sie auf "Execute"
   - **Kopieren Sie den `accessToken` aus der Antwort**

#### Schritt 2: JWT-Token in Swagger konfigurieren

1. **Klicken Sie auf den "Authorize" Button** (🔓 Symbol) oben rechts in Swagger UI
2. **Geben Sie den Token ein** im Format: `Bearer YOUR_ACCESS_TOKEN`
   ```
   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
3. **Klicken Sie auf "Authorize"**
4. **Klicken Sie auf "Close"**

> ✅ **Sie sind jetzt authentifiziert** und können alle API-Endpoints testen!

### 🛠️ Verfügbare API-Endpunkte

#### **Benutzer (Users)**
- `POST /api/v1/users/register` - Benutzerregistrierung (öffentlich)
- `POST /api/v1/users/login` - Benutzeranmeldung (öffentlich)
- `GET /api/v1/users/me` - Aktuelle Benutzerinformationen
- `DELETE /api/v1/users` - Benutzer löschen

#### **Wirkstoffe (Active Substances)**
- `GET /api/v1/active-substances` - Alle Wirkstoffe abrufen
- `POST /api/v1/active-substances` - Neuen Wirkstoff erstellen
- `GET /api/v1/active-substances/{id}` - Wirkstoff nach ID abrufen
- `PATCH /api/v1/active-substances/{id}` - Wirkstoff aktualisieren
- `DELETE /api/v1/active-substances/{id}` - Wirkstoff löschen

#### **Alterskategorien (Age Categories)**
- `GET /api/v1/age-categories` - Alle Alterskategorien abrufen
- `POST /api/v1/age-categories` - Neue Alterskategorie erstellen
- `GET /api/v1/age-categories/{id}` - Alterskategorie nach ID abrufen
- `PATCH /api/v1/age-categories/{id}` - Alterskategorie aktualisieren
- `DELETE /api/v1/age-categories/{id}` - Alterskategorie löschen

#### **Impfstoff-Typen (Vaccine Types)**
- `GET /api/v1/vaccine-types` - Alle Impfstoff-Typen abrufen
- `POST /api/v1/vaccine-types` - Neuen Impfstoff-Typ erstellen
- `GET /api/v1/vaccine-types/{id}` - Impfstoff-Typ nach ID abrufen
- `PATCH /api/v1/vaccine-types/{id}` - Impfstoff-Typ aktualisieren
- `DELETE /api/v1/vaccine-types/{id}` - Impfstoff-Typ löschen

#### **Impfpläne (Immunization Plans)**
- `GET /api/v1/immunization-plans` - Alle Impfpläne abrufen
- `POST /api/v1/immunization-plans` - Neuen Impfplan erstellen
- `GET /api/v1/immunization-plans/{id}` - Impfplan nach ID abrufen
- `PATCH /api/v1/immunization-plans/{id}` - Impfplan aktualisieren
- `DELETE /api/v1/immunization-plans/{id}` - Impfplan löschen
- `GET /api/v1/immunization-plans/by-vaccine-type/{vaccineTypeId}` - Filter nach Impfstoff-Typ
- `GET /api/v1/immunization-plans/by-age-category/{ageCategoryId}` - Filter nach Alterskategorie

#### **Impfungen (Immunization Records)**
- `GET /api/v1/immunization-records` - Alle Impfungen abrufen
- `POST /api/v1/immunization-records` - Neue Impfung erstellen
- `GET /api/v1/immunization-records/{id}` - Impfung nach ID abrufen
- `PATCH /api/v1/immunization-records/{id}` - Impfung aktualisieren
- `DELETE /api/v1/immunization-records/{id}` - Impfung löschen
- `GET /api/v1/immunization-records/by-user/{userId}` - Impfungen nach Benutzer
- `GET /api/v1/immunization-records/by-vaccine-type/{vaccineTypeId}` - Filter nach Impfstoff-Typ
- `GET /api/v1/immunization-records/by-user/{userId}/vaccine-type/{vaccineTypeId}` - Kombinierte Filter

### 💡 Tipps für die Nutzung von Swagger UI

1. **Beispielwerte verwenden**: Swagger UI generiert automatisch Beispielwerte für alle Felder
2. **Validierung testen**: Probieren Sie ungültige Daten aus, um die Validierung zu testen
3. **Response-Codes verstehen**: Beachten Sie die verschiedenen HTTP-Status-Codes (200, 201, 400, 404, etc.)
4. **Filter-Endpunkte nutzen**: Testen Sie die speziellen Filter-Endpunkte für erweiterte Abfragen
5. **Fehlerbehandlung**: API-Fehler werden strukturiert im `ExceptionMessageBodyDto` Format zurückgegeben

### 🔍 Beispiel-Workflow

1. **Anmelden** und Token erhalten
2. **Wirkstoff erstellen**: `POST /api/v1/active-substances`
3. **Alterskategorie erstellen**: `POST /api/v1/age-categories`
4. **Impfstoff-Typ erstellen**: `POST /api/v1/vaccine-types`
5. **Impfplan erstellen**: `POST /api/v1/immunization-plans`
6. **Impfung dokumentieren**: `POST /api/v1/immunization-records`
7. **Daten abrufen**: Nutzen Sie die verschiedenen GET-Endpunkte und Filter

### ⚙️ Swagger-Konfiguration

Die Swagger-Dokumentation ist in der `application.yaml` konfiguriert:

```yaml
# SpringDoc OpenAPI Configuration
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: alpha
    tags-sorter: alpha
    display-request-duration: true
    disable-swagger-default-url: true
  show-actuator: false
  group-configs:
    - group: 'default'
      paths-to-match: '/api/**'
      packages-to-scan: ch.ffhs.spring_boosters.controller
```

## 📊 Datenmodell

Das System basiert auf einem hierarchischen Impfmanagement-Modell:

### 🧬 Kernentitäten

#### 1. **Vaccine Type** (Impfstofftyp)
Verschiedene Impfstoffe wie Biontech, Moderna, AstraZeneca, Johnson & Johnson.
- `vaccine_type_id` - Eindeutige ID
- `name` - Name des Impfstoffs
- `code` - Impfstoff-Code

#### 2. **Age Category** (Alterskategorie)
Altersgruppen für zielgerichtete Impfpläne.
- `age_category_id` - Eindeutige ID
- `name` - Beschreibung der Altersgruppe
- `age_min_days` - Mindestalter in Tagen
- `age_max_days` - Höchstalter in Tagen (NULL = offen)

**Beispiele:**
- 0-17 Jahre (Kinder/Jugendliche)
- 18-29 Jahre (Junge Erwachsene)
- 30-39 Jahre, 40-49 Jahre, etc.
- 80+ Jahre (Senioren)

#### 3. **Immunization Plan** (Impfplan)
Verknüpfung zwischen Impfstoff und Altersgruppe.
- `immunization_plan_id` - Eindeutige ID
- `vaccine_type_id` - Referenz zum Impfstofftyp
- `age_category_id` - Referenz zur Alterskategorie
- `name` - Name des Plans

#### 4. **Immunization Plan Series** (Impfserie)
Definition der Dosierungsreihenfolge für einen Impfplan.
- `immunization_plan_series_id` - Eindeutige ID
- `immunization_plan_id` - Referenz zum Impfplan
- `series_name` - Name der Serie
- `required_doses` - Anzahl erforderlicher Dosen

**Beispiel:**
```
Biontech Impfplan für 18-29 Jahre:
├── Serie: "Grundimmunisierung"
│   ├── 1. Dosis (Tag 0)
│   └── 2. Dosis (Tag 21)
└── Serie: "Auffrischung"
    └── 3. Dosis (nach 6 Monaten)
```

#### 5. **Follow-up Rule** (Nachfolgeregeln)
Regeln, die definieren, wann nach einem abgeschlossenen Impfplan (Plan A) der nächste Plan (Plan B) fällig wird.
- `from_plan_id` - Ausgangsplan
- `to_plan_id` - Zielplan
- `required_series_id` - Erforderliche abgeschlossene Serie
- `min_completed_doses` - Mindestanzahl abgeschlossener Dosen
- `target_min_age_days` - Mindestalter für Zielplan
- `min_interval_days_since_last` - Mindestabstand seit letzter Dosis

#### 6. **Active Substance** (Wirkstoffe)
Aktive Bestandteile der Impfstoffe.
- `active_substance_id` - Eindeutige ID
- `name` - Name des Wirkstoffs
- `synonyms` - Alternative Bezeichnungen

#### 7. **User** (Benutzer)
Systembenutzer für Authentifizierung und Personalisierung.
- `id` - Eindeutige UUID
- `username` - Benutzername
- `password_hash` - Verschlüsseltes Passwort
- `first_name`, `last_name` - Name
- `birth_date` - Geburtsdatum
- `role` - Benutzerrolle

#### 8. **Immunization Record** (Impfaufzeichnung)
Dokumentation tatsächlich verabreichter Impfungen.
- `id` - Eindeutige UUID
- `user_id` - Referenz zum Benutzer
- `vaccine_type_id` - Verabreichter Impfstoff
- `immunization_plan_id` - Zugehöriger Impfplan
- `administered_on` - Verabreichungsdatum
- `dose_order_claimed` - Dosis-Nummer in der Serie

## 🔐 API Authentifizierung

Das System verwendet JWT (JSON Web Token) für die Authentifizierung.

### Verfügbare Endpoints

#### Öffentliche Endpoints (keine Authentifizierung erforderlich)
- `POST /api/auth/register` - Benutzerregistrierung
- `POST /api/auth/login` - Benutzeranmeldung

#### Geschützte Endpoints (JWT Token erforderlich)
- `GET /api/auth/me` - Aktuelle Benutzerinformationen
- `DELETE /api/auth` - Benutzer löschen
- Weitere API-Endpoints für Impfplan-Management

### Verwendung

1. **Registrierung**: Erstellen Sie einen Account über `/api/auth/register`
2. **Anmeldung**: Erhalten Sie einen JWT-Token über `/api/auth/login`
3. **API-Zugriff**: Verwenden Sie den Token im `Authorization: Bearer <token>` Header

## 🧪 Testing

### API-Tests
Im `API Requests` Ordner finden Sie vorgefertigte HTTP-Dateien:
- `auth-register.http` - Benutzerregistrierung
- `auth-login.http` - Benutzeranmeldung  
- `auth-delete-user.http` - Vollständiger Test-Workflow
- `api-requests.http` - Allgemeine API-Tests

### Integration Tests
```bash
mvn test
```

## 🛠️ Technologie-Stack

- **Backend**: Spring Boot 3.4.8
- **Security**: Spring Security mit JWT
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Testing**: TestContainers
- **Build**: Maven
- **Containerization**: Docker

## 📁 Projektstruktur

```
spring_boosters/
├── src/main/java/ch/ffhs/spring_boosters/
│   ├── config/                     # Konfigurationen
│   ├── controller/                 # REST Controllers
    │   ├── dto/                    # Data Transfer Objects
    │   ├── entity/                 # JPA Entitäten
    │   ├── mapper/                 # DTO ↔ Entity Mapping
│   ├── repository/                 # Datenzugriff
│   └── service/                    # Business Logic
|   └── security/                   # Sicherheitskonfiguration
├── src/main/resources/
│   ├── db/migration/   # Flyway SQL Scripts
│   └── application.yaml
├── docker/             # Docker Konfiguration
└── API Requests/       # HTTP Test Files
```

## 🔧 Konfiguration

Die Anwendung wird über `application.yaml` konfiguriert:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/spring_booster_db
    username: postgres
    password: postgres
  
jwt:
  secret: mySecretJWTKey123456789SpringBootersFFHS2025
  expiration: 86400000 # 24 Stunden
```

## 🤝 Contributing

1. Fork das Repository
2. Erstellen Sie einen Feature-Branch
3. Committen Sie Ihre Änderungen
4. Erstellen Sie eine Pull Request

## 📄 Lizenz

Dieses Projekt ist Teil einer FFHS-Projektarbeit.

---

**Entwickelt mit ❤️ für das FFHS Spring Boosters Projekt**
