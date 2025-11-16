# Build Stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Nur pom.xml für schnellen Dependency Cache
COPY pom.xml .
RUN mvn -q -e -U dependency:go-offline

# Quelle kopieren
COPY src ./src

# Build (Tests überspringen für schnelleren Container-Bau)
RUN mvn -q -DskipTests package

# Run Stage (schlankes JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8000
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
