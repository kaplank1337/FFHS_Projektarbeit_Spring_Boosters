# -------- Build Stage --------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Nur pom.xml zuerst für Dependency-Caching
COPY pom.xml .
RUN mvn -q -e -U dependency:go-offline

# Quellcode kopieren
COPY src ./src

# Jar bauen (Tests überspringen für schnelleres Image)
RUN mvn -q -DskipTests package

# -------- Run Stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Artefakt aus Build Stage kopieren
COPY --from=build /app/target/core_backend-0.0.1-SNAPSHOT.jar app.jar

# Port & Runtime Optionen
EXPOSE 8081
ENV JAVA_OPTS=""

# Startkommando
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
