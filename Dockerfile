# ---------- Stage 1: build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom first so dependencies are cached between builds
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy the source and build the jar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as a non-root user and give it a writable logs directory
RUN groupadd --system spring && useradd --system --gid spring spring \
    && mkdir -p /app/tlogs \
    && chown -R spring:spring /app
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]