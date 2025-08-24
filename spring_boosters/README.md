Datenbank Container bauen und starten:
 - Via CLI in das Pfad navivigieren, in dem das Dockerfile liegt
 - "docker build -t spring-booster-postgres:17" ausführen
 - Anschliessend um den Container zu starten "docker run --name spring-booster-db -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=spring_booster_db -v spring_booster_pgdata:/var/lib/postgresql/data -d spring-booster-postgres:17
   "
