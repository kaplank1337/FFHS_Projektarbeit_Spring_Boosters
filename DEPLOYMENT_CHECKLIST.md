# Railway Deployment Checklist

## ✅ Was wurde geändert (Code)

### 1. CORS-Konfiguration aktualisiert
- **Datei:** `authentification_service/src/main/java/ch/ffhs/authentification_service/security/SecurityConfig.java`
- **Änderung:** CORS erlaubt jetzt spezifische Origins (nicht mehr `*`)
- **Grund:** `allowCredentials(true)` + `*` funktioniert nicht zusammen

### 2. Application.yaml erweitert
- **Datei:** `authentification_service/src/main/resources/application.yaml`
- **Änderung:** Neue Property `cors.allowed-origins`
- **Default:** `http://localhost:3000` für lokale Entwicklung

### 3. Frontend .env Dateien
- **Neu:** `frontend/.env.example`
- **Neu:** `frontend/.env.production`
- **Geändert:** `frontend/.gitignore` (schützt .env Dateien)

---

## 🚀 Deployment-Schritte (GENAU IN DIESER REIHENFOLGE!)

### Schritt 1: Code committen und pushen

```bash
cd /Users/ninoziswiler/Documents/Coding/01_FFHS/FFHS_Projektarbeit_Spring_Boosters

# Status prüfen
git status

# Alle Änderungen hinzufügen
git add .

# Commit
git commit -m "fix: Configure CORS for Railway deployment

- Add CORS_ALLOWED_ORIGINS environment variable support
- Update SecurityConfig to use specific origins instead of wildcard
- Add frontend .env files
- Update Railway configuration documentation"

# Push
git push
```

### Schritt 2: Railway Variablen setzen

#### 2.1 Frontend
```
Railway → frontend → Variables → New Variable

Variable: VITE_API_BASE_URL
Value: https://authentification-service-production.up.railway.app
```
(Ersetze mit deiner tatsächlichen Auth-Service-URL!)

#### 2.2 Authentification Service
```
Railway → authentification_service → Variables → New Variable

Variable: CORS_ALLOWED_ORIGINS
Value: https://spring-boosters.up.railway.app,http://localhost:3000
```
(Ersetze mit deiner tatsächlichen Frontend-URL!)

**Weitere Variablen für authentification_service:**
```
CORE_BACKEND_HOST=corebackend.railway.internal
CORE_BACKEND_PORT=8081
NOTIFICATION_SERVICE_HOST=notification_service.railway.internal
NOTIFICATION_SERVICE_PORT=8082
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
JWT_EXPIRATION_SECONDS=3600
```

#### 2.3 Core Backend
```
Railway → corebackend → Variables

SPRING_DATASOURCE_URL=jdbc:postgresql://divine-nurturing.railway.internal:5432/spring_booster_db
NOTIFICATION_SERVICE_HOST=notification_service.railway.internal
NOTIFICATION_SERVICE_PORT=8082
JWT_SECRET=f79ca3e9583d0451b249e87d8b1c3ead2bf190dd55c6a95e0f884713b89f109a1d217ad1e76accb3cdafb96c09bed2fe27762f347e53a1596ddc88a4973bcb51
```
(Ersetze `divine-nurturing.railway.internal` mit deinem DB Private Network Namen!)

#### 2.4 Notification Service
```
Railway → notification_service → Variables

SPRING_DATASOURCE_URL=jdbc:postgresql://divine-nurturing.railway.internal:5432/spring_booster_db?currentSchema=notification_service
```
(Ersetze `divine-nurturing.railway.internal` mit deinem DB Private Network Namen!)

### Schritt 3: Services neu deployen

Railway deployed automatisch nach dem Git Push. Prüfe den Status:

1. **authentification_service** → **Deployments** → Status prüfen
2. Warte bis Status = **"Online"** (ca. 2-3 Min)
3. **frontend** → **Deployments** → Status prüfen
4. Warte bis Status = **"Online"** (ca. 1-2 Min)

### Schritt 4: Öffentliche Domain generieren (falls noch nicht gemacht)

```
Railway → authentification_service → Settings → Networking
→ Public Networking → "Generate Domain"
```

Kopiere die generierte URL und aktualisiere `VITE_API_BASE_URL` im Frontend!

### Schritt 5: Testen

1. Öffne dein Frontend: `https://spring-boosters.up.railway.app`
2. Öffne Browser DevTools (F12) → Console
3. Versuche dich zu registrieren
4. **Sollte jetzt funktionieren ohne CORS-Fehler!** ✅

---

## 🔍 Troubleshooting

### CORS-Fehler immer noch da?

**1. Prüfe die Logs:**
```
Railway → authentification_service → Logs
```

Suche nach Startup-Logs mit CORS-Konfiguration.

**2. Prüfe die Umgebungsvariablen:**
```
Railway → authentification_service → Variables
```

Stelle sicher `CORS_ALLOWED_ORIGINS` ist korrekt gesetzt.

**3. Hard Refresh im Browser:**
- Chrome/Edge: `Ctrl+Shift+R` (Windows) oder `Cmd+Shift+R` (Mac)
- Firefox: `Ctrl+F5` (Windows) oder `Cmd+Shift+R` (Mac)

**4. Prüfe die URLs:**
- Frontend-URL in `CORS_ALLOWED_ORIGINS` muss **exakt** übereinstimmen
- Kein trailing slash: `https://spring-boosters.up.railway.app` (nicht `/` am Ende)
- Immer `https://` für Railway URLs (nicht `http://`)

### Backend kann Datenbank nicht erreichen?

```
Railway → database → Settings → Networking → Private Networking
```

Kopiere den **Private Network Namen** (z.B. `divine-nurturing.railway.internal`) und setze ihn in:
- `corebackend` → `SPRING_DATASOURCE_URL`
- `notification_service` → `SPRING_DATASOURCE_URL`

### 401 Unauthorized?

Stelle sicher, dass `JWT_SECRET` in **corebackend** und **authentification_service** **identisch** ist!

---

## 📝 Variablen-Übersicht

| Service | Variable | Beispiel-Wert |
|---------|----------|---------------|
| **frontend** | `VITE_API_BASE_URL` | `https://auth-xyz.up.railway.app` |
| **authentification_service** | `CORS_ALLOWED_ORIGINS` | `https://frontend.up.railway.app,http://localhost:3000` |
| **authentification_service** | `CORE_BACKEND_HOST` | `corebackend.railway.internal` |
| **authentification_service** | `JWT_SECRET` | `<long-secret-string>` |
| **corebackend** | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db.railway.internal:5432/spring_booster_db` |
| **corebackend** | `JWT_SECRET` | `<same-as-auth-service>` |
| **notification_service** | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db.railway.internal:5432/spring_booster_db?currentSchema=notification_service` |

---

## ✅ Finale Checkliste

- [ ] Code commited und gepusht
- [ ] `VITE_API_BASE_URL` im Frontend gesetzt
- [ ] `CORS_ALLOWED_ORIGINS` im Auth-Service gesetzt
- [ ] Alle anderen Variablen gesetzt (siehe oben)
- [ ] Öffentliche Domain für Auth-Service generiert
- [ ] Services sind "Online"
- [ ] Frontend getestet
- [ ] CORS-Fehler ist weg
- [ ] Login/Registrierung funktioniert

---

## 📚 Weitere Hilfe

- **CORS-Fix Details:** Siehe `RAILWAY_CORS_FIX.md`
- **Vollständige Config:** Siehe `RAILWAY_CONFIG.md`
- **Quick Start:** Siehe `RAILWAY_QUICKSTART.md`

---

**Nach Completion dieser Checklist sollte alles funktionieren!** 🎉
