Datenbank Container bauen und starten (WICHTIG! Wird auf PORT 5434 gestartet, da 5432 schon durch Docker Desktop belegt ist):
 - Via CLI in das Pfad navivigieren, in dem das Dockerfile liegt
 - "docker build -t spring-boosters-db ." (Vom Dockerfile Pfad aus) ausführen
 - Anschliessend um den Container zu starten "docker run -d -p 5434:5432 --name spring-booster-db"
