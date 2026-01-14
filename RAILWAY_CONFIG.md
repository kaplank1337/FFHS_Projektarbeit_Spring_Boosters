# Railway Deployment - Umgebungsvariablen Konfiguration

## Wichtig: API Gateway braucht öffentliche Domain!

**Der `authentification_service` ist dein API Gateway und MUSS eine öffentliche Domain haben!**

### Schritt 1: Öffentliche Domain für authentification_service generieren

1. Gehe zu **authentification_service** in Railway
2. Klicke auf **Settings** Tab
3. Unter **Networking** → **Public Networking**
4. Klicke auf **"Generate Domain"**
5. Du erhältst eine URL wie: `authentification-service-production.up.railway.app`

**Diese URL brauchst du für das Frontend!**

---

## Umgebungsvariablen für alle Services

### 1. Frontend Service

Gehe zu **frontend** → **Variables** Tab und setze:

```bash
# API Gateway URL (WICHTIG: Verwende die generierte URL von authentification_service!)
VITE_API_BASE_URL=https://authentification-service-production.up.railway.app

# Vite Build-Konfiguration
NODE_ENV=production
```

**WICHTIG:** Ersetze `authentification-service-production.up.railway.app` mit der **tatsächlichen** generierten Domain deines authentification_service!

---

### 2. Authentification Service (API Gateway)

Gehe zu **authentification_service** → **Variables** Tab:

```bash
# Server Port
SERVER_PORT=8000

# Core Backend Connection (Private Network)
CORE_BACKEND_HOST=corebackend.railway.internal
CORE_BACKEND_PORT=8081

# Notification Service Connection (Private Network)
NOTIFICATION_SERVICE_HOST=notification_service.railway.internal
NOTIFICATION_SERVICE_PORT=8082

# JWT Configuration
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
JWT_EXPIRATION_SECONDS=3600

# CORS Configuration (WICHTIG für Frontend-Zugriff!)
CORS_ALLOWED_ORIGINS=https://spring-boosters.up.railway.app,http://localhost:3000

# Java Opts
JAVA_OPTS=-Xmx512m -Xms256m
```

**WICHTIG:**
- Ersetze `https://spring-boosters.up.railway.app` mit deiner **tatsächlichen Frontend-URL**
- Mehrere Origins mit Komma trennen (keine Leerzeichen!)
- `http://localhost:3000` für lokale Entwicklung

---

### 3. Core Backend Service

Gehe zu **corebackend** → **Variables** Tab:

```bash
# Server Port
SERVER_PORT=8081

# Database Connection (Railway Private Network)
# WICHTIG: Verwende den Private Network Namen von deiner Database!
SPRING_DATASOURCE_URL=jdbc:postgresql://divine-nurturing.railway.internal:5432/spring_booster_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}

# Hinweis: ${POSTGRES_PASSWORD} wird automatisch von Railway gesetzt, wenn du die DB verlinkt hast

# Hibernate/JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false

# Notification Service Connection (Private Network)
NOTIFICATION_SERVICE_HOST=notification_service.railway.internal
NOTIFICATION_SERVICE_PORT=8082

# JWT Configuration (gleicher Secret wie Gateway!)
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
JWT_EXPIRATION_SECONDS=3600

# Java Opts
JAVA_OPTS=-Xmx512m -Xms256m
```

**WICHTIG:**
- Ersetze `divine-nurturing.railway.internal` mit dem **tatsächlichen** Private Network Namen deiner Datenbank!
- Dieser ist in deinem Screenshot: `divine-nurturing.railway.internal`

---

### 4. Notification Service

Gehe zu **notification_service** → **Variables** Tab:

```bash
# Server Port
SERVER_PORT=8082

# Database Connection (Railway Private Network)
SPRING_DATASOURCE_URL=jdbc:postgresql://divine-nurturing.railway.internal:5432/spring_booster_db?currentSchema=notification_service
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}

# Hibernate/JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false

# Mail Configuration (Optional - nur wenn du E-Mail verwenden willst)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=noreply.springboosters@gmail.com
# MAIL_PASSWORD=<dein-app-passwort>  # Nur setzen wenn du E-Mail brauchst

# Java Opts
JAVA_OPTS=-Xmx512m -Xms256m
```

---

### 5. Database (PostgreSQL)

Die Database braucht normalerweise nur die Standard-Variablen, die Railway automatisch setzt:

```bash
POSTGRES_DB=spring_booster_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=<wird-von-railway-generiert>
```

**Railway setzt diese automatisch, du musst nichts machen!**

---

## Schritt-für-Schritt Anleitung

### Phase 1: API Gateway öffentlich machen

1. **authentification_service** öffnen
2. **Settings** Tab
3. **Networking** → **Public Networking**
4. **"Generate Domain"** klicken
5. **URL kopieren** (z.B. `https://authentification-service-production.up.railway.app`)

### Phase 2: Variablen setzen (in dieser Reihenfolge!)

#### 1. Database Variablen prüfen
- Gehe zu **database** → **Variables**
- Prüfe, dass `POSTGRES_DB=spring_booster_db` gesetzt ist
- Kopiere den **Private Network Namen** (z.B. `divine-nurturing.railway.internal`)

#### 2. Core Backend konfigurieren
- Gehe zu **corebackend** → **Variables**
- Füge alle Variablen aus Abschnitt 3 hinzu
- **Ersetze** `divine-nurturing.railway.internal` mit deinem tatsächlichen DB-Namen
- Klicke **"Add"** für jede Variable

#### 3. Notification Service konfigurieren
- Gehe zu **notification_service** → **Variables**
- Füge alle Variablen aus Abschnitt 4 hinzu
- **Ersetze** `divine-nurturing.railway.internal` mit deinem tatsächlichen DB-Namen
- Klicke **"Add"** für jede Variable

#### 4. Authentification Service konfigurieren
- Gehe zu **authentification_service** → **Variables**
- Füge alle Variablen aus Abschnitt 2 hinzu
- Klicke **"Add"** für jede Variable

#### 5. Frontend konfigurieren
- Gehe zu **frontend** → **Variables**
- Setze `VITE_API_BASE_URL` auf die **öffentliche URL** von authentification_service
- **WICHTIG:** URL muss mit `https://` beginnen!
- Beispiel: `VITE_API_BASE_URL=https://authentification-service-production.up.railway.app`

### Phase 3: Services neu deployen

Nach dem Setzen der Variablen müssen alle Services neu gestartet werden:

1. **corebackend** → **Deployments** → Neuestes Deployment → **"Redeploy"**
2. **notification_service** → **Deployments** → Neuestes Deployment → **"Redeploy"**
3. **authentification_service** → **Deployments** → Neuestes Deployment → **"Redeploy"**
4. **frontend** → **Deployments** → Neuestes Deployment → **"Redeploy"**

**Wichtig:** Warte nach jedem Redeploy 1-2 Minuten, bis der Service "Online" ist!

### Phase 4: Testen

1. Öffne die **Frontend URL**: `https://spring-boosters.up.railway.app`
2. Versuche dich zu registrieren/einloggen
3. Prüfe die **Logs** wenn etwas nicht funktioniert:
   - **frontend** → **Logs**
   - **authentification_service** → **Logs**
   - **corebackend** → **Logs**

---

## Wichtige Private Network URLs

Basierend auf deinen Screenshots:

| Service | Private Network URL | Port | Öffentlich? |
|---------|---------------------|------|-------------|
| database | `divine-nurturing.railway.internal` | 5432 | ❌ Nein |
| corebackend | `corebackend.railway.internal` | 8081 | ❌ Nein |
| notification_service | `notification_service.railway.internal` | 8082 | ❌ Nein |
| authentification_service | `authentification_service.railway.internal` | 8000 | ✅ **JA - Domain generieren!** |
| frontend | - | 3000 | ✅ Ja (bereits vorhanden) |

---

## Troubleshooting

### Problem: Frontend kann Backend nicht erreichen

**Lösung:**
1. Prüfe, dass `authentification_service` eine **öffentliche Domain** hat
2. Prüfe, dass `VITE_API_BASE_URL` im Frontend auf diese Domain zeigt
3. Stelle sicher, dass die URL mit `https://` beginnt (nicht `http://`)

### Problem: Backend kann Datenbank nicht erreichen

**Lösung:**
1. Prüfe, dass der **Private Network Name** korrekt ist
2. In Railway: **database** → **Settings** → **Networking** → **Private Networking**
3. Kopiere den Namen und setze ihn in `SPRING_DATASOURCE_URL`

### Problem: 401 Unauthorized Fehler

**Lösung:**
1. Prüfe, dass `JWT_SECRET` in **corebackend** und **authentification_service** **identisch** ist
2. Stelle sicher, dass beide Services neu deployed wurden

### Problem: Services starten nicht

**Lösung:**
1. Prüfe die **Logs** des Services
2. Häufige Fehler:
   - Datenbank-Connection-String falsch
   - Port bereits in Verwendung
   - Fehlende Umgebungsvariablen

### Problem: CORS Fehler im Frontend

**Lösung:**
1. Prüfe, dass der `authentification_service` CORS für die Frontend-URL erlaubt
2. In der Spring Boot Config sollte CORS für `*.railway.app` erlaubt sein

---

## Schnell-Checkliste

- [ ] Database Private Network Name kopiert
- [ ] Authentification Service **öffentliche Domain generiert**
- [ ] Authentification Service Public URL kopiert
- [ ] Core Backend Variablen gesetzt (inkl. DB URL)
- [ ] Notification Service Variablen gesetzt (inkl. DB URL)
- [ ] Authentification Service Variablen gesetzt
- [ ] Frontend `VITE_API_BASE_URL` auf Auth Service URL gesetzt
- [ ] Alle Services redeployed
- [ ] Frontend geöffnet und getestet

---

## Beispiel: Vollständige URLs nach Setup

Angenommen, deine generierte Auth Service URL ist `https://auth.up.railway.app`:

**Frontend Config:**
```
VITE_API_BASE_URL=https://auth.up.railway.app
```

**Frontend ruft auf:**
```
https://spring-boosters.up.railway.app → Ruft auf → https://auth.up.railway.app/api/v1/...
```

**Auth Service leitet weiter an:**
```
https://auth.up.railway.app → Leitet intern weiter → corebackend.railway.internal:8081
```

**Core Backend kommuniziert mit:**
```
corebackend.railway.internal:8081 → Verbindet zu → divine-nurturing.railway.internal:5432 (Database)
corebackend.railway.internal:8081 → Verbindet zu → notification_service.railway.internal:8082
```

---

**Wichtig:** Railway verwendet **interne DNS** für Private Networking. Deshalb funktionieren die `.railway.internal` URLs nur **zwischen Services innerhalb von Railway**!

Das Frontend läuft im **Browser des Users**, deshalb braucht es eine **öffentliche URL** für den API Gateway (authentification_service).
