FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

# Cache dependencies and build
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre

# Create non-root user and app directory
RUN useradd -m -u 1000 app || true
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/target/*.jar app.jar
RUN chown -R app:app /app
USER app

# Container-friendly JVM options (tune via JAVA_OPTS env)
ENV JAVA_OPTS "-XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75 -XX:MinRAMPercentage=10 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080

# Healthcheck against actuator (uses curl present in image)
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
