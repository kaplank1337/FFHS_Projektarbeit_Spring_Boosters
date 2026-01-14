# Railway Quick Start - In 5 Minuten fertig!

## Das Problem

Dein Frontend auf Railway zeigt noch auf `localhost:8000` statt auf deine Railway-Services.

## Die Lösung (3 Schritte)

### ✅ Schritt 1: Öffentliche Domain für API Gateway generieren

Dein **authentification_service** ist der API Gateway und braucht eine öffentliche URL:

1. Gehe zu Railway → **authentification_service**
2. Klicke auf **Settings** Tab
3. Scrolle zu **Networking** → **Public Networking**
4. Klicke **"Generate Domain"**
5. **Kopiere die generierte URL** (z.B. `https://authentification-service-production-abc123.up.railway.app`)

---

### ✅ Schritt 2: Umgebungsvariablen setzen

#### Frontend (WICHTIGSTE Variable!)

1. Gehe zu **frontend** → **Variables** Tab
2. Klicke **"New Variable"**
3. Setze:
   ```
   Variable: VITE_API_BASE_URL
   Value: <die-kopierte-URL-aus-Schritt-1>
   ```
   **Beispiel:** `https://authentification-service-production-abc123.up.railway.app`

4. Klicke **"Add"**

#### Authentification Service

1. Gehe zu **authentification_service** → **Variables** Tab
2. Füge folgende Variablen hinzu (klicke für jede "New Variable"):

```
CORE_BACKEND_HOST=corebackend.railway.internal
CORE_BACKEND_PORT=8081
NOTIFICATION_SERVICE_HOST=notification_service.railway.internal
NOTIFICATION_SERVICE_PORT=8082
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
JWT_EXPIRATION_SECONDS=3600
CORS_ALLOWED_ORIGINS=https://spring-boosters.up.railway.app,http://localhost:3000
```

**WICHTIG:** Ersetze `https://spring-boosters.up.railway.app` mit deiner tatsächlichen Frontend-URL!

#### Core Backend

1. Gehe zu **corebackend** → **Variables** Tab
2. **WICHTIG:** Kopiere zuerst den Database Private Network Namen:
   - Gehe zu **database** → **Settings** → **Networking** → **Private Networking**
   - Kopiere den Namen (z.B. `divine-nurturing.railway.internal`)

3. Füge folgende Variablen hinzu:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://divine-nurturing.railway.internal:5432/spring_booster_db
NOTIFICATION_SERVICE_HOST=notification_service.railway.internal
NOTIFICATION_SERVICE_PORT=8082
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
```

**WICHTIG:** Ersetze `divine-nurturing.railway.internal` mit deinem tatsächlichen DB Namen!

#### Notification Service

1. Gehe zu **notification_service** → **Variables** Tab
2. Füge folgende Variablen hinzu:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://divine-nurturing.railway.internal:5432/spring_booster_db?currentSchema=notification_service
```

**WICHTIG:** Ersetze `divine-nurturing.railway.internal` mit deinem tatsächlichen DB Namen!

---

### ✅ Schritt 3: Services neu deployen

Nach dem Setzen der Variablen müssen die Services neu starten:

1. **frontend** → **Deployments** → Neuestes Deployment → Klicke auf ⋮ → **"Redeploy"**
2. Warte bis Status = **"Online"** (ca. 1-2 Min)

---

## Fertig! 🎉

Öffne dein Frontend: `https://spring-boosters.up.railway.app`

Es sollte jetzt funktionieren!

---

## Wenn es nicht funktioniert

### Logs prüfen

1. **frontend** → **Logs** → Suche nach Fehlern
2. **authentification_service** → **Logs** → Suche nach Connection-Fehlern

### Häufige Fehler

**Frontend zeigt noch localhost:**
- ✅ Hast du `VITE_API_BASE_URL` gesetzt?
- ✅ Hast du das Frontend **redeployed**?
- ✅ Beginnt die URL mit `https://` (nicht `http://`)?

**401 Unauthorized:**
- ✅ Ist `JWT_SECRET` in corebackend und authentification_service **identisch**?

**Cannot connect to database:**
- ✅ Ist der Database Private Network Name korrekt?
- ✅ Format: `jdbc:postgresql://<db-name>.railway.internal:5432/spring_booster_db`

---

## Zusammenfassung - Was macht was?

```
User Browser
    ↓
Frontend (spring-boosters.up.railway.app)
    ↓ (VITE_API_BASE_URL)
Authentification Service (öffentlich: auth-xyz.up.railway.app)
    ↓ (privat: corebackend.railway.internal)
Core Backend
    ↓ (privat: divine-nurturing.railway.internal)
PostgreSQL Database
```

**Wichtig:**
- **Öffentlich** = Von überall erreichbar (braucht Generate Domain)
- **Privat** = Nur zwischen Railway Services (`.railway.internal`)

---

Für Details siehe: **RAILWAY_CONFIG.md**
