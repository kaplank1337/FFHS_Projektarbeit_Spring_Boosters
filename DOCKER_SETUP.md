# Spring Boosters - Docker Compose Setup

## Beschreibung
Dieses docker-compose.yml orchestriert die gesamte Spring Boosters Microservice-Architektur:

- **PostgreSQL** (Port 5432): Gemeinsame Datenbank für alle Services
- **core_backend** (Port 8081): Hauptlogik, User-Verwaltung, Impfungen
- **notification_service** (Port 8082): E-Mail Benachrichtigungen
- **authentification_service** (Port 8000): API Gateway + JWT Authentifizierung

## Voraussetzungen
- Docker Desktop installiert und gestartet
- Docker Compose V2+ (integriert in Docker Desktop)
- Mind. 4GB freier RAM
- Optional: `.env` Datei für Mail-Credentials

## Umgebungsvariablen (optional)

Erstelle eine `.env` Datei im Projektroot für sensible Daten:

```env
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
```

## Befehle

### Erstmaliges Starten (mit Build)
```bash
docker compose up --build -d
```

### Nur starten (Images existieren bereits)
```bash
docker compose up -d
```

### Logs verfolgen
```bash
# Alle Services
docker compose logs -f

# Einzelner Service
docker compose logs -f core_backend
docker compose logs -f authentification_service
docker compose logs -f notification_service
docker compose logs -f postgres
```

### Status prüfen
```bash
docker compose ps
```

### Stoppen (Container bleiben erhalten)
```bash
docker compose stop
```

### Stoppen und entfernen (Volumes bleiben erhalten)
```bash
docker compose down
```

### Komplett zurücksetzen (inkl. Volumes/Daten)
```bash
docker compose down -v
```

### Neu bauen (ohne Cache)
```bash
docker compose build --no-cache
docker compose up -d
```

## Service URLs

Nach erfolgreichem Start:

- **API Gateway**: http://localhost:8000
- **Core Backend API**: http://localhost:8081
- **Core Backend Swagger**: http://localhost:8081/swagger-ui.html
- **Notification Service**: http://localhost:8082
- **PostgreSQL**: localhost:5432 (User: postgres, Password: postgres, DB: spring_booster_db)

## Schnelltests

### 1. Health Check
```bash
curl http://localhost:8000/actuator/health
curl http://localhost:8081/v3/api-docs
```

### 2. User registrieren (über Gateway)
```bash
curl -X POST http://localhost:8000/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "testuser",
    "passwordHash": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User",
    "birthDate": "1990-01-01"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "testuser",
    "password": "SecurePass123!"
  }'
```

### 4. Swagger UI öffnen
```bash
open http://localhost:8081/swagger-ui.html
```

## Troubleshooting

### Services starten nicht
```bash
# Logs überprüfen
docker compose logs core_backend

# Container einzeln neu starten
docker compose restart core_backend
```

### Datenbank-Probleme
```bash
# DB Container neu starten
docker compose restart postgres

# DB Logs prüfen
docker compose logs postgres
```

### Port bereits belegt
Passe Ports in docker-compose.yml an:
```yaml
ports:
  - "8181:8081"  # Statt 8081:8081
```

### Build-Fehler
```bash
# Cache leeren und neu bauen
docker builder prune -f
docker compose build --no-cache
```

### Netzwerk-Probleme
```bash
# Netzwerk neu erstellen
docker compose down
docker network prune -f
docker compose up -d
```

## Architektur-Übersicht

```
┌─────────────────────────────────────────────────────┐
│  Client (Browser/Postman/curl)                      │
└────────────────────┬────────────────────────────────┘
                     │ HTTP
                     ▼
         ┌───────────────────────┐
         │ authentification_     │
         │ service :8000         │
         │ (Gateway + JWT)       │
         └───────┬───────────┬───┘
                 │           │
       ┌─────────┘           └──────────┐
       │ /api/**                         │ /notifications/**
       ▼                                 ▼
┌──────────────┐                  ┌────────────────┐
│ core_backend │                  │ notification_  │
│ :8081        │                  │ service :8082  │
└──────┬───────┘                  └────────┬───────┘
       │                                   │
       └────────────┬──────────────────────┘
                    ▼
            ┌──────────────┐
            │  PostgreSQL  │
            │  :5432       │
            └──────────────┘
```

## Weitere Informationen

- **PostgreSQL Schema**: `public` (core_backend), `notification_service` (notification_service)
- **Flyway Migrationen**: Automatisch beim Start ausgeführt
- **JWT Token Expiration**: 3600 Sekunden (1 Stunde)
- **Health Checks**: Postgres, Core Backend

## Entwicklung

Für lokale Entwicklung außerhalb von Docker siehe README.md der jeweiligen Services.

