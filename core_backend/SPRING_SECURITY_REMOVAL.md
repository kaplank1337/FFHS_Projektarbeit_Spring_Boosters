# Spring Security Entfernung aus core_backend

## Übersicht

Spring Security wurde vollständig aus dem `core_backend` Service entfernt. Die Authentifizierung und Autorisierung wird jetzt ausschließlich über den Gateway Service (`authentification_service`) abgewickelt.

## Durchgeführte Änderungen

### 1. **pom.xml**
✅ Entfernt:
- Spring Security Starter Dependency
- JWT Dependencies (jjwt-api, jjwt-impl, jjwt-jackson)

### 2. **UserServiceImpl.java**
✅ Entfernt:
- `UserDetailsService` Interface Implementation
- `PasswordEncoder` Dependency
- `loadUserByUsername()` Methode
- Password Hashing Logik (wird jetzt vom Gateway gemacht)

✅ Hinzugefügt:
- Kommentar: "Passwort wird bereits vom Gateway gehasht"

### 3. **UserController.java**
✅ Entfernt:
- `AuthenticationManager` Dependency
- `JwtUtil` Dependency
- `SecurityContextHolder` Aufrufe
- `Authentication` und `Principal` Handling
- Spring Security Imports

✅ Geändert:
- **POST /api/v1/auth/login**: Prüft nur noch ob User existiert, Authentifizierung erfolgt im Gateway
- **GET /api/v1/auth/me**: Verwendet `X-User-Id` Header statt SecurityContext
- **DELETE /api/v1/auth/{userId}**: Nimmt userId als Path Parameter statt aus Principal

### 4. **ImmunizationRecordController.java**
✅ Entfernt:
- Spring Security Imports
- `Principal` Parameter
- `UsernamePasswordAuthenticationToken` Casting

✅ Geändert:
- **DELETE /{id}**: Verwendet `X-User-Id` Header statt Principal
- **GET /myVaccinations**: Verwendet `X-User-Id` Header statt Principal

### 5. **UserService.java**
✅ Hinzugefügt:
- `findById(UUID userId)` Methode zum Interface

## Neue Architektur

```
┌─────────────────────────────────────────────────────────┐
│                   API Gateway                            │
│          (authentification_service)                      │
│                                                          │
│  • JWT Token Generierung                                │
│  • JWT Token Validierung                                │
│  • Spring Security                                       │
│  • Password Hashing                                      │
│  • Setzt X-User-Id Header                               │
└─────────────────────┬────────────────────────────────────┘
                      │
                      │ X-User-Id Header
                      ▼
┌─────────────────────────────────────────────────────────┐
│               core_backend Service                       │
│                                                          │
│  • Keine Spring Security                                │
│  • Keine JWT Verarbeitung                               │
│  • Liest X-User-Id aus Header                           │
│  • Business Logik                                        │
└─────────────────────────────────────────────────────────┘
```

## Header Convention

Der Gateway Service setzt nach erfolgreicher JWT-Validierung folgende Header:

```
X-User-Id: <UUID des authentifizierten Users>
```

Der core_backend Service liest diesen Header und verwendet ihn für:
- Autorisierung (welcher User darf was?)
- User-spezifische Daten laden
- Audit Logging

## Vorteile dieser Architektur

1. **Separation of Concerns**: 
   - Gateway = Authentifizierung/Autorisierung
   - Backend = Business Logik

2. **Single Source of Truth**: 
   - JWT Logik nur an einem Ort (Gateway)

3. **Einfachere Services**: 
   - Backend Services müssen sich nicht um Security kümmern

4. **Skalierbarkeit**: 
   - Backend Services können unabhängig skaliert werden

5. **Wartbarkeit**: 
   - Security Updates nur im Gateway nötig

## Migration Checklist

- [x] Spring Security Dependencies entfernt
- [x] JWT Dependencies entfernt
- [x] UserDetailsService Interface entfernt
- [x] AuthenticationManager Verwendung entfernt
- [x] SecurityContext Verwendung entfernt
- [x] Principal Parameter ersetzt durch Header
- [x] UserService Interface erweitert (findById)
- [x] Kompilierung erfolgreich
- [x] Package Build erfolgreich

## Testing

### Lokales Testen mit Header

```bash
# Registrierung (über Gateway)
curl -X POST http://localhost:8000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.user",
    "password": "Test1234!",
    "firstName": "Test",
    "lastName": "User",
    "birthDate": "1990-01-01"
  }'

# Login (über Gateway, erhält JWT Token)
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.user",
    "password": "Test1234!"
  }'

# Geschützter Endpoint (über Gateway mit JWT)
curl -X GET http://localhost:8000/api/v1/auth/me \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Direkt am Backend (nur für Testing, in Produktion über Gateway!)
curl -X GET http://localhost:8082/api/v1/auth/me \
  -H "X-User-Id: <USER_UUID>"
```

## Wichtige Hinweise

⚠️ **Der core_backend Service sollte in Produktion NICHT direkt erreichbar sein!**

- Alle Requests müssen über den Gateway laufen
- Gateway validiert JWT und setzt `X-User-Id` Header
- Backend vertraut dem Header (da nur vom Gateway gesetzt)

⚠️ **In Produktion**: Netzwerk-Isolation zwischen Gateway und Backend Services verwenden (z.B. Docker Network, Kubernetes Service Mesh)

## Nächste Schritte

1. ✅ core_backend kompiliert ohne Spring Security
2. ⏭️ Docker Image neu bauen
3. ⏭️ docker-compose.yml aktualisieren
4. ⏭️ Gateway konfigurieren um X-User-Id Header zu setzen
5. ⏭️ Integration Testing durchführen

## Status

✅ **ABGESCHLOSSEN** - Spring Security wurde erfolgreich aus core_backend entfernt!

Build Status: **SUCCESS**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

