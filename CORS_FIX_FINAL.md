# CORS Fix - Finale Lösung für Spring Cloud Gateway

## Was war das Problem?

Spring Cloud Gateway braucht eine **eigene** CORS-Konfiguration, weil das Gateway die Requests abfängt, **BEVOR** sie zu den Security-Filtern kommen.

## Was wurde geändert?

### 1. Neue Datei: `CorsConfig.java`
Eine dedizierte CORS-Konfiguration, die:
- Die `CORS_ALLOWED_ORIGINS` Variable aus application.yaml liest
- Komma-separierte Origins parsed
- Einen `CorsWebFilter` Bean erstellt

### 2. `SecurityConfig.java` bereinigt
- CORS-bezogener Code entfernt (war nicht wirksam wegen Gateway)
- Nur noch Security-Konfiguration

### 3. `application.yaml` bereinigt
- Gateway-CORS-Konfiguration entfernt
- Nur noch `cors.allowed-origins` Property für die CorsConfig

---

## So deployed du das JETZT:

### Schritt 1: Änderungen committen

```bash
cd /Users/ninoziswiler/Documents/Coding/01_FFHS/FFHS_Projektarbeit_Spring_Boosters

git add .
git commit -m "fix: Add proper CORS configuration for Spring Cloud Gateway"
git push origin railway
```

### Schritt 2: Railway Variable setzen (falls noch nicht gemacht)

1. Gehe zu Railway Dashboard
2. Klicke auf **authentification_service**
3. Gehe zu **Variables** Tab
4. Klicke **"New Variable"**
5. Füge hinzu:

```
Variable: CORS_ALLOWED_ORIGINS
Value: https://spring-boosters.up.railway.app,http://localhost:3000
```

**WICHTIG:** Ersetze `https://spring-boosters.up.railway.app` mit deiner **tatsächlichen** Frontend-URL!

### Schritt 3: Warten auf Deployment

Railway deployed automatisch nach dem Push. Prüfe:

1. **authentification_service** → **Deployments**
2. Warte bis Status = **"Online"** (ca. 2-3 Minuten)
3. Prüfe die **Logs** für Fehler:
   ```
   Railway → authentification_service → Logs
   ```

### Schritt 4: Testen

1. Öffne dein Frontend: `https://spring-boosters.up.railway.app`
2. Öffne Browser DevTools (F12) → **Console**
3. Versuche dich zu registrieren oder einzuloggen
4. **Sollte jetzt ohne CORS-Fehler funktionieren!** ✅

---

## Überprüfen ob es funktioniert

### Im Browser (DevTools → Network Tab):

1. Öffne DevTools (F12)
2. Gehe zu **Network** Tab
3. Versuche Login/Registrierung
4. Klicke auf den Request (z.B. `login`)
5. Gehe zu **Headers** Tab
6. Prüfe **Response Headers**:

**Du solltest sehen:**
```
Access-Control-Allow-Origin: https://spring-boosters.up.railway.app
Access-Control-Allow-Credentials: true
```

**NICHT mehr:**
```
No 'Access-Control-Allow-Origin' header is present
```

---

## Troubleshooting

### CORS-Fehler immer noch da?

#### 1. Prüfe die Railway Variable

```
Railway → authentification_service → Variables → CORS_ALLOWED_ORIGINS
```

Stelle sicher:
- Variable existiert
- Wert ist korrekt: `https://spring-boosters.up.railway.app,http://localhost:3000`
- **Keine Leerzeichen** zwischen URLs!
- URL beginnt mit `https://` (nicht `http://` für Railway!)

#### 2. Prüfe die Logs

```
Railway → authentification_service → Logs
```

Suche nach:
- Startup-Logs
- CORS-bezogene Fehler
- Bean creation errors

#### 3. Hard Refresh im Browser

CORS-Header werden oft gecached:

- **Chrome/Edge:** `Ctrl+Shift+R` (Windows) oder `Cmd+Shift+R` (Mac)
- **Firefox:** `Ctrl+F5` (Windows) oder `Cmd+Shift+R` (Mac)

Oder noch besser: **Incognito/Private Mode** verwenden!

#### 4. Prüfe die URL-Übereinstimmung

Die Frontend-URL in `CORS_ALLOWED_ORIGINS` muss **EXAKT** übereinstimmen:

✅ Richtig:
```
https://spring-boosters.up.railway.app
```

❌ Falsch:
```
https://spring-boosters.up.railway.app/     (trailing slash!)
http://spring-boosters.up.railway.app       (http statt https!)
spring-boosters.up.railway.app              (fehlendes https://)
```

#### 5. Deployment erfolgreich?

```
Railway → authentification_service → Deployments
```

Prüfe:
- Neuestes Deployment ist **"Online"**
- Build hatte keine Fehler
- Deployment ist nach deinem Git-Push

---

## Wie die CORS-Konfiguration funktioniert

### CorsConfig.java
```java
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;  // Liest aus application.yaml

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // Parse komma-separierte Origins
        String[] origins = allowedOrigins.split(",");
        for (String origin : origins) {
            config.addAllowedOrigin(origin.trim());
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // ... registriert für alle Pfade
    }
}
```

### application.yaml
```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

**Lookup-Reihenfolge:**
1. Railway Variable `CORS_ALLOWED_ORIGINS` (wenn gesetzt)
2. Sonst Default: `http://localhost:3000`

---

## Warum funktioniert es jetzt?

### Vorher (nicht funktionierend):
```
Browser Request
    ↓
Spring Cloud Gateway (keine CORS-Header)  ❌
    ↓
SecurityConfig CorsWebFilter (wird nie erreicht!)
    ↓
Backend
```

### Nachher (funktionierend):
```
Browser Request
    ↓
Spring Cloud Gateway
    ↓
CorsConfig CorsWebFilter (fügt CORS-Header hinzu)  ✅
    ↓
SecurityConfig (Security-Check)
    ↓
Backend
```

**Der CorsWebFilter läuft VOR dem Gateway-Routing** und fügt die notwendigen CORS-Header zur Response hinzu!

---

## Zusammenfassung

✅ `CorsConfig.java` erstellt (dedizierte CORS-Konfiguration)
✅ `SecurityConfig.java` bereinigt (CORS entfernt)
✅ `application.yaml` bereinigt (Gateway-CORS entfernt)
✅ `CORS_ALLOWED_ORIGINS` Variable in Railway setzen
✅ Code committen und pushen
✅ Warten auf Deployment
✅ Testen!

**Danach sollte der CORS-Fehler endgültig verschwunden sein!** 🎉
