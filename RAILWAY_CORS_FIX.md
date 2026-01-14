# Railway CORS Fix - Sofort-Lösung

## Problem
```
Access to fetch at 'https://authentification-service-production.up.railway.app/api/v1/auth/register'
from origin 'https://spring-boosters.up.railway.app' has been blocked by CORS policy
```

## Ursache
Der `authentification_service` erlaubt noch nicht das Frontend als Origin.

## Lösung (2 Schritte)

### Schritt 1: CORS Variable in Railway setzen

1. Gehe zu Railway Dashboard → **authentification_service**
2. Klicke auf **Variables** Tab
3. Klicke **"New Variable"**
4. Füge hinzu:

```
Variable: CORS_ALLOWED_ORIGINS
Value: https://spring-boosters.up.railway.app,http://localhost:3000
```

**Wichtig:**
- Verwende deine **tatsächliche Frontend-URL** (siehe in deinem Railway Dashboard)
- Mehrere Origins mit **Komma** trennen (keine Leerzeichen!)
- `http://localhost:3000` ist für lokale Entwicklung

**Beispiel mit korrekten URLs:**
```
CORS_ALLOWED_ORIGINS=https://spring-boosters.up.railway.app,http://localhost:3000
```

### Schritt 2: Code neu deployen

Die Code-Änderungen müssen deployed werden:

1. **Commit & Push** den aktualisierten Code:
   ```bash
   cd /Users/ninoziswiler/Documents/Coding/01_FFHS/FFHS_Projektarbeit_Spring_Boosters
   git add .
   git commit -m "fix: Configure CORS for Railway deployment"
   git push
   ```

2. **Warte auf Auto-Deploy** (Railway deployed automatisch nach Push)
   - Oder manuell: **authentification_service** → **Deployments** → **"Redeploy"**

### Schritt 3: Testen

1. Warte bis Deployment **"Online"** ist (1-2 Min)
2. Öffne dein Frontend: `https://spring-boosters.up.railway.app`
3. Versuche dich zu registrieren
4. **Sollte jetzt funktionieren!** ✅

---

## Was wurde geändert?

### 1. SecurityConfig.java (authentification_service)
```java
// NEU: CORS Origins aus Umgebungsvariable
@Value("${cors.allowed-origins:http://localhost:3000}")
private String allowedOrigins;

@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);

    // NEU: Parse comma-separated origins
    String[] origins = allowedOrigins.split(",");
    for (String origin : origins) {
        config.addAllowedOrigin(origin.trim());
    }

    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addExposedHeader("Authorization");

    // ... rest of config
}
```

### 2. application.yaml (authentification_service)
```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

---

## Warum war das Problem?

**Vorher:**
```java
config.setAllowCredentials(true);
config.addAllowedOriginPattern("*");  // ❌ Funktioniert NICHT mit credentials!
```

**Nachher:**
```java
config.setAllowCredentials(true);
config.addAllowedOrigin("https://spring-boosters.up.railway.app");  // ✅ Spezifische Origin
```

**CORS-Regel:**
Wenn `allowCredentials = true`, dann **MUSS** die Origin spezifisch sein (nicht `*`).
Dies ist aus Sicherheitsgründen so.

---

## Für lokale Entwicklung

Wenn du lokal entwickeln willst, setze in deiner lokalen `.env` oder direkt beim Start:

```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

Oder in der application.yaml ist bereits `http://localhost:3000` als Default gesetzt.

---

## Troubleshooting

### CORS-Fehler immer noch da?

**1. Prüfe die Logs:**
```
Railway → authentification_service → Logs
```

Suche nach:
```
Allowed origins: [https://spring-boosters.up.railway.app, ...]
```

**2. Prüfe die Variable:**
```
Railway → authentification_service → Variables
```

Stelle sicher:
- `CORS_ALLOWED_ORIGINS` ist gesetzt
- Wert ist korrekt (mit `https://`)
- Keine Leerzeichen in der Liste

**3. Prüfe Frontend-URL:**
Stelle sicher, dass die URL in `CORS_ALLOWED_ORIGINS` **exakt** mit der Frontend-URL übereinstimmt:
- `https://spring-boosters.up.railway.app` (kein trailing slash!)
- Nicht `http://` verwenden für Railway URLs (immer `https://`)

**4. Hard Refresh im Browser:**
```
Chrome/Edge: Ctrl+Shift+R (Windows) oder Cmd+Shift+R (Mac)
Firefox: Ctrl+F5 (Windows) oder Cmd+Shift+R (Mac)
```

---

## Zusammenfassung

✅ Code aktualisiert (CORS-Konfiguration flexibel gemacht)
✅ `CORS_ALLOWED_ORIGINS` Variable in Railway setzen
✅ Code committen und pushen
✅ Warten auf Deployment
✅ Testen!

**Die Änderung ist flexibel:** Du kannst jetzt beliebige Origins über die Railway Variable hinzufügen, ohne den Code ändern zu müssen.
