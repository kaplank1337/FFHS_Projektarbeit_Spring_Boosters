# Implementierung: Ausstehende Impfungen

## Übersicht

Ich habe eine vollständige Implementierung für die Berechnung und Abfrage von ausstehenden Impfungen erstellt. Benutzer können jetzt sehen, welche Impfungen sie noch nachholen sollten.

## Neue Dateien

### 1. DTOs (Data Transfer Objects)

#### `/controller/dto/ImmunizationScheduleDto.java`
- Hauptresponse-Objekt mit allen Informationen zu ausstehenden Impfungen
- Enthält Benutzerinfo, aktuelles Alter, Liste der Impfungen und Zusammenfassung

#### `/controller/dto/PendingImmunizationDto.java`
- Detaillierte Informationen zu einer einzelnen ausstehenden Impfung
- Enthält Impfplan, Impfstoff, Alterskategorie, Dosen, Priorität

### 2. Service Layer

#### `/service/ImmunizationScheduleService.java`
- Interface für den Service

#### `/service/implementation/ImmunizationScheduleServiceImpl.java`
- Komplette Businesslogik zur Berechnung ausstehender Impfungen
- Algorithmus:
  1. Benutzer laden und Alter berechnen
  2. Bereits erfasste Impfungen gruppieren
  3. Relevante Alterskategorien ermitteln
  4. Für jeden Impfplan prüfen: 
     - Ist im relevanten Altersbereich?
     - Sind genügend Dosen erfasst?
     - Priorität berechnen (HIGH/MEDIUM/LOW)
  5. Nach Priorität sortieren

### 3. Controller

#### `/controller/ImmunizationScheduleController.java` (aktualisiert)
- **GET** `/api/v1/immunization-schedule/users/{userId}/pending`
  - Vollständige Liste aller ausstehenden Impfungen
- **GET** `/api/v1/immunization-schedule/users/{userId}/pending/summary`
  - Kompakte Zusammenfassung (nur Zähler)

### 4. Test-Requests

#### `/API Requests/ImmunizationScheduleController/immunization-schedule-requests.http`
- Beispiel HTTP-Requests zum Testen der Endpoints

#### `/API Requests/ImmunizationScheduleController/README.md`
- Ausführliche Dokumentation der API
- Beispiel-Responses
- Erklärung der Logik und Prioritäten

## Funktionsweise

### Prioritäten-Berechnung

**HIGH (Hoch)**
- Impfung ist überfällig
- Grundimmunisierung sollte bereits begonnen haben, wurde aber noch nicht gestartet

**MEDIUM (Mittel)**
- Benutzer ist im empfohlenen Altersbereich
- Impfung sollte bald durchgeführt werden

**LOW (Niedrig)**
- Impfung ist für die Zukunft geplant
- Benutzer ist noch nicht im Altersbereich (aber innerhalb 90 Tage)

### Relevanz-Prüfung

Eine Impfung wird als relevant eingestuft, wenn:
- Aktuelles Alter ist im Bereich [`ageMinDays`, `ageMaxDays`]
- ODER Alter wird bald im Bereich sein (innerhalb 90 Tage)
- ODER Alter hat Bereich kürzlich überschritten (Grace Period: 365 Tage)

### Dosierung

Das System berücksichtigt:
- Erforderliche Dosen aus `immunization_plan_series.required_doses`
- Bereits erfasste Dosen aus `immunization_record`
- Fehlende Dosen werden berechnet

## Verwendung

### 1. Alle ausstehenden Impfungen abrufen

```bash
curl -X GET "http://localhost:8082/api/v1/immunization-schedule/users/{userId}/pending" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "userId": "...",
  "username": "...",
  "birthDate": "1990-05-15",
  "currentAgeDays": 12450,
  "totalPending": 5,
  "highPriority": 2,
  "mediumPriority": 2,
  "lowPriority": 1,
  "pendingImmunizations": [
    {
      "immunizationPlanName": "MMR Grundimmunisierung",
      "vaccineTypeName": "Masern-Mumps-Röteln",
      "reason": "Grundimmunisierung",
      "recommendedDoses": 2,
      "completedDoses": 0,
      "missingDoses": 2,
      "priority": "HIGH",
      "overdue": true
    }
  ]
}
```

### 2. Zusammenfassung abrufen

```bash
curl -X GET "http://localhost:8082/api/v1/immunization-schedule/users/{userId}/pending/summary" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "userId": "...",
  "username": "...",
  "totalPending": 5,
  "highPriority": 2,
  "mediumPriority": 2,
  "lowPriority": 1,
  "currentAgeDays": 12450
}
```

## Integration mit bestehenden Funktionen

Die neue API integriert sich nahtlos mit bestehenden Funktionen:

1. **User Management**: Nutzt bestehende `User` Entity und `UserRepository`
2. **Immunization Records**: Berücksichtigt erfasste Impfungen aus `ImmunizationRecord`
3. **Immunization Plans**: Verwendet die konfigurierten Impfpläne
4. **Age Categories**: Filtert nach relevanten Alterskategorien

## Testing

1. **Benutzer erstellen**
   ```
   POST /api/v1/auth/register
   ```

2. **Geburtsdatum prüfen**
   ```
   GET /api/v1/users/{userId}
   ```

3. **Ausstehende Impfungen abrufen**
   ```
   GET /api/v1/immunization-schedule/users/{userId}/pending
   ```

4. **Impfung erfassen**
   ```
   POST /api/v1/immunization-records
   ```

5. **Erneut prüfen** (Liste sollte aktualisiert sein)
   ```
   GET /api/v1/immunization-schedule/users/{userId}/pending
   ```

## Swagger Documentation

Die API ist vollständig in Swagger dokumentiert. Nach dem Start der Anwendung verfügbar unter:

```
http://localhost:8082/swagger-ui.html
```

Suchen Sie nach dem Tag "Impfplan" um die neuen Endpoints zu finden.

## Erweiterungsmöglichkeiten

Die Implementierung kann erweitert werden mit:

1. **Follow-up Rules**: Berücksichtigung von `follow_up_rule` für Auffrischungen
2. **Benachrichtigungen**: Integration mit `notification_service` für Erinnerungen
3. **Zeitplan**: Empfohlene Termine für zukünftige Impfungen
4. **Export**: PDF-Export des Impfplans
5. **Statistiken**: Dashboard für Impfstatus

## Hinweise

- Die Berechnung erfolgt **in Echtzeit** bei jedem Request
- Keine Caching-Strategie implementiert (kann bei Bedarf hinzugefügt werden)
- Alle Altersberechnungen basieren auf **Tagen** (nicht Monaten/Jahren)
- Die Grace Period (365 Tage) nach Alters-Maximum kann konfiguriert werden

