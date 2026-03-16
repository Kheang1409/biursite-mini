FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre

RUN groupadd -g 1000 app || true \
	&& useradd -m -u 1000 -g 1000 -s /bin/sh app || true
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/target/*.jar app.jar
# Ensure ownership is correct and tolerant if group/user already exist
# Use numeric UID fallback to avoid runtime user lookup failures
RUN chown -R 1000:1000 /app || true
# Use numeric UID (1000) at runtime to avoid "no matching entries in passwd file"
USER 1000

ENV JAVA_OPTS "-XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75 -XX:MinRAMPercentage=10 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
