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
│   ├── config/          # Security & JWT Konfiguration
│   ├── controller/      # REST Controllers
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA Entitäten
│   ├── mapper/         # DTO ↔ Entity Mapping
│   ├── repository/     # Datenzugriff
│   └── service/        # Business Logic
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
