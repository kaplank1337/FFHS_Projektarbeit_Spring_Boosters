# Authentication Service (API Gateway)

Dieses Modul stellt das zentrale API Gateway der Spring Boosters Anwendung dar. Es nimmt alle HTTP‑Requests von außen entgegen, validiert sie mittels JWT und leitet sie an die internen Services weiter (z. B. `core_backend`, `notification_service`).

## Architekturüberblick

- **Framework:** Spring Boot 3 / Java 21
- **API Gateway:** Spring Cloud Gateway (WebFlux‑basiert)
- **Security:** Spring Security + JWT (io.jsonwebtoken / jjwt)
- **Funktion:**
  - Exponiert einen einzigen Einstiegspunkt (Standard: `http://localhost:8000`)
  - Überprüft eingehende JWT‑Tokens
  - Leitet valide Requests an interne Services weiter
  - Kann zentral Logging und Cross‑Cutting Concerns übernehmen

### Wichtige Klassen

- `AuthentificationServiceApplication`
  - Spring‑Boot Einstiegspunkt.
- `security.JwtAuthenticationFilter`
  - Liest das `Authorization: Bearer <token>` Header.
  - Validiert das Token mit `JwtValidator`.
  - Schreibt die Authentifizierungsinformationen in den Security‑Kontext.
- `security.JwtValidator`
  - Kapselt die Logik zum Signieren und Verifizieren von JWTs.
  - Nutzt das Secret aus der Konfiguration (`jwt.secret`).
- `security.SecurityConfig`
  - Konfiguriert Spring Security für Gateway‑Anfragen.
  - Definiert, welche Pfade geschützt sind und wie das Filter‑Chain aufgebaut ist.
- `logger.LoggingWebFilter`
  - Ein WebFlux‑Filter, der eingehende Requests protokolliert.

## Konfiguration

### `application.yaml`

Die wichtigste Konfiguration liegt in `src/main/resources/application.yaml`:

- **Server Port**
  - Default: `8000`
  - Konfigurierbar per Umgebungsvariable `SERVER_PORT`.

```yaml
server:
  port: ${SERVER_PORT:8000}
```

- **JWT Einstellungen**

```yaml
jwt:
  secret: ${JWT_SECRET:...}
  expiration-seconds: ${JWT_EXPIRATION_SECONDS:3600}
```

- `jwt.secret`: HMAC‑Secret für das Signieren und Prüfen der Tokens.
- `jwt.expiration-seconds`: Gültigkeitsdauer der Tokens in Sekunden.

- **Gateway Routing**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: core-backend
          uri: http://corebackend:8081
          predicates:
            - Path=/**
```

- Alle eingehenden Requests an das Gateway (`/`) werden an den Service `corebackend:8081` weitergeleitet.
- In einem Docker‑Setup liegt `corebackend` im gleichen Docker‑Netzwerk wie dieses Gateway und ist nur intern erreichbar.

## Lokale Entwicklung (ohne Docker)

### Voraussetzungen

- Java 21 (z. B. Temurin 21)
- Maven 3.9+

### Anwendung starten

Im Projekt‑Root des Moduls (`authentification_service`):

```bash
./mvnw spring-boot:run
```

Danach ist der Gateway‑Service erreichbar unter:

- `http://localhost:8000`

Andere Services (z. B. `core_backend`) müssen separat laufen und unter der im YAML konfigurierten URL erreichbar sein (lokal meist `http://localhost:8081`).

## Docker

Dieses Modul ist für den Docker‑Betrieb vorbereitet und wird typischerweise zusammen mit den anderen Services über `docker-compose` gestartet.

### Dockerfile Aufbau

Das `Dockerfile` befindet sich im Root des Moduls und nutzt einen 2‑stufigen Build:

1. **Build Stage** (Maven + JDK)
   - Basis: `maven:3.9.9-eclipse-temurin-21`
   - Lädt Abhängigkeiten und baut das JAR.
2. **Run Stage** (schlankes JRE)
   - Basis: `eclipse-temurin:21-jre`
   - Kopiert das gebaute JAR.
   - Startet die Anwendung auf Port `8000`.

### Image lokal bauen

Im Modul‑Verzeichnis:

```bash
# Image bauen
docker build -t authentification_service .
```

### Container lokal starten

Minimalbeispiel (ohne weitere Services):

```bash
docker run \
  -p 8000:8000 \
  -e SERVER_PORT=8000 \
  -e JWT_SECRET="dein-sicheres-jwt-secret" \
  --name authentification_service \
  authentification_service
```

Danach ist das Gateway unter `http://localhost:8000` erreichbar.

In einem vollständigen Setup wird dieser Container zusammen mit `core_backend` und `notification_service` in einem gemeinsamen Docker‑Netzwerk betrieben. Nur der Port 8000 des Gateways wird nach außen gemappt; die anderen Services bleiben intern.

Beispiel (vereinfacht):

```yaml
services:
  authentification_service:
    build: ./authentification_service
    ports:
      - "8000:8000"
    environment:
      - SERVER_PORT=8000
      - JWT_SECRET=dein-sicheres-jwt-secret
    depends_on:
      - corebackend

  corebackend:
    build: ./core_backend
    expose:
      - "8081"
```

## Logging

Das Modul enthält einen `LoggingWebFilter`, der HTTP‑Requests protokolliert. Zusätzlich kann das Logging‑Level in der `application.yaml` oder über Umgebungsvariablen angepasst werden, z. B.:

```yaml
logging:
  level:
    root: INFO
    ch.ffhs.authentification_service: DEBUG
```

Dadurch lassen sich Requests und Authentifizierungsfehler im Log nachvollziehen.

## Tests

Im Ordner `src/test/java` befinden sich Unit‑ und Konfigurationstests, u. a.:

- `AuthentificationServiceApplicationTests`
- `security.JwtAuthenticationFilterTest`
- `security.JwtValidatorTest`
- `security.SecurityConfigTest`

Ausführen der Tests:

```bash
./mvnw test
```

## Nutzung als API Gateway

- Alle externen Clients sprechen ausschließlich das Gateway an (`http://localhost:8000`).
- Das Gateway validiert JWT‑Tokens und gibt Anfragen nur weiter, wenn das Token gültig ist.
- Interne Services (z. B. `core_backend`, `notification_service`) sind nur für das Gateway sichtbar und nicht direkt von außen erreichbar.

Dies ermöglicht:

- Zentrale Authentifizierung und Autorisierung
- Konsistentes Logging und Monitoring
- Saubere Trennung zwischen öffentlich erreichbarem Endpoint und interner Service‑Landschaft.

