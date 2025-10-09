# Notification Service - Spring Boot E-Mail Service

## Beschreibung
Dieser Service versendet E-Mail-Benachrichtigungen über anstehende Impfungen. Die Daten werden über eine REST-Schnittstelle empfangen und als HTML-formatierte E-Mail mit einer Tabelle der Impfungen versendet.

## Features
- ✉️ E-Mail-Versand mit HTML-Template (Thymeleaf)
- 📊 Tabelle mit Impfungsinformationen
- 💾 Logging aller versendeten E-Mails in PostgreSQL
- 🔄 REST-API für E-Mail-Versand
- ✅ Fehlerbehandlung und Statusmeldungen
- 🗄️ **Flyway Database Migrations** für versionierte Schema-Verwaltung

## Technologien
- Spring Boot 3.5.6
- Spring Mail
- Thymeleaf Template Engine
- PostgreSQL
- JPA/Hibernate
- **Flyway** für Datenbankmigrationen

## Datenbank-Migration mit Flyway

### Migration Scripts
Die Datenbank wird automatisch durch Flyway-Migrationen erstellt:

- **V1__create_schema.sql** - Erstellt das `notification_service` Schema
- **V2__create_email_log_table.sql** - Erstellt die `email_log` Tabelle mit Indexen

Beim Start der Anwendung führt Flyway automatisch alle ausstehenden Migrationen aus.

### Flyway Konfiguration
- **Schema:** `notification_service`
- **Migrations-Ordner:** `src/main/resources/db/migration`
- **Naming Convention:** `V{version}__{description}.sql`
- **Hibernate ddl-auto:** `validate` (Flyway übernimmt die Schema-Verwaltung)

### Neue Migration hinzufügen
Um eine neue Migration hinzuzufügen:
1. Erstelle eine neue SQL-Datei in `src/main/resources/db/migration`
2. Benne sie nach dem Muster: `V3__deine_beschreibung.sql`
3. Flyway führt sie beim nächsten Start automatisch aus

## Konfiguration

### E-Mail Konfiguration
Setze die folgenden Umgebungsvariablen für Gmail:

```bash
set MAIL_USERNAME=deine-email@gmail.com
set MAIL_PASSWORD=dein-app-passwort
```

**Wichtig für Gmail:**
1. Aktiviere 2-Faktor-Authentifizierung in deinem Google-Konto
2. Erstelle ein App-Passwort unter: https://myaccount.google.com/apppasswords
3. Verwende dieses App-Passwort (nicht dein normales Passwort)

### Für andere E-Mail-Provider
Passe in `application.yaml` folgende Werte an:
- `spring.mail.host` (z.B. smtp.outlook.com, smtp.office365.com)
- `spring.mail.port` (z.B. 587, 465)

## Start der Anwendung

```bash
mvnw.cmd spring-boot:run
```

Die Anwendung läuft auf: `http://localhost:8081`

## REST API

### E-Mail senden

**Endpoint:** `POST /api/v1/email/send`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "recipientEmail": "empfaenger@example.com",
  "recipientName": "Max Mustermann",
  "subject": "Ihre anstehenden Impfungen",
  "vaccinations": [
    {
      "vaccineName": "Tetanus",
      "dueDate": "2025-12-31",
      "status": "pending",
      "description": "Auffrischungsimpfung alle 10 Jahre empfohlen"
    },
    {
      "vaccineName": "Influenza",
      "dueDate": "2025-11-15",
      "status": "urgent",
      "description": "Jährliche Grippeimpfung empfohlen"
    },
    {
      "vaccineName": "COVID-19",
      "dueDate": "2025-10-20",
      "status": "pending",
      "description": "Booster-Impfung"
    }
  ]
}
```

**Response (Erfolg):**
```json
{
  "success": true,
  "message": "E-Mail erfolgreich gesendet an empfaenger@example.com",
  "timestamp": "2025-10-09T14:30:00"
}
```

**Response (Fehler):**
```json
{
  "success": false,
  "message": "Fehler beim Senden der E-Mail: Connection timeout",
  "timestamp": "2025-10-09T14:30:00"
}
```

## Beispiel mit cURL

```bash
curl -X POST http://localhost:8081/api/v1/email/send ^
  -H "Content-Type: application/json" ^
  -d "{\"recipientEmail\":\"test@example.com\",\"recipientName\":\"Max Mustermann\",\"subject\":\"Ihre anstehenden Impfungen\",\"vaccinations\":[{\"vaccineName\":\"Tetanus\",\"dueDate\":\"2025-12-31\",\"status\":\"pending\",\"description\":\"Auffrischungsimpfung empfohlen\"}]}"
```

## Beispiel mit PowerShell

```powershell
$body = @{
    recipientEmail = "test@example.com"
    recipientName = "Max Mustermann"
    subject = "Ihre anstehenden Impfungen"
    vaccinations = @(
        @{
            vaccineName = "Tetanus"
            dueDate = "2025-12-31"
            status = "pending"
            description = "Auffrischungsimpfung empfohlen"
        },
        @{
            vaccineName = "Influenza"
            dueDate = "2025-11-15"
            status = "urgent"
            description = "Jährliche Grippeimpfung"
        }
    )
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/email/send" -Method Post -Body $body -ContentType "application/json"
```

## Status-Werte für Impfungen
- `pending` - Ausstehend (orange)
- `urgent` - Dringend (rot)
- `completed` - Abgeschlossen (grün)

## E-Mail Template
Das HTML-Template befindet sich unter: `src/main/resources/templates/vaccination-email.html`

Features des Templates:
- Responsive Design
- Professionelles Styling
- Farbcodierte Status-Anzeige
- Tabelle mit allen Impfungsinformationen

## Datenbank
Alle versendeten E-Mails werden in der Tabelle `email_log` gespeichert:
- Empfänger-Informationen
- E-Mail-Inhalt
- Zeitstempel
- Erfolgsstatus
- Fehlermeldungen (falls vorhanden)

## Troubleshooting

### E-Mails werden nicht versendet
1. Überprüfe die E-Mail-Konfiguration in `application.yaml`
2. Stelle sicher, dass die Umgebungsvariablen gesetzt sind
3. Prüfe die Firewall-Einstellungen (Port 587 muss offen sein)
4. Bei Gmail: Verwende ein App-Passwort

### Datenbankfehler
1. Stelle sicher, dass PostgreSQL läuft
2. Überprüfe die Datenbankverbindung in `application.yaml`
3. Die Tabelle wird automatisch erstellt (ddl-auto: update)

## Entwickelt von
Spring Boosters Team - FFHS Projektarbeit
-- SQL Script zum Erstellen der email_log Tabelle
-- Falls die Tabelle nicht automatisch erstellt wird

CREATE TABLE IF NOT EXISTS email_log (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    sent_at TIMESTAMP NOT NULL,
    success BOOLEAN NOT NULL,
    error_message TEXT
);

-- Index für häufige Abfragen
CREATE INDEX idx_email_log_recipient ON email_log(recipient_email);
CREATE INDEX idx_email_log_sent_at ON email_log(sent_at);
