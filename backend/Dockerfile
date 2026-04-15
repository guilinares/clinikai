# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and build files first (layer cache)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Download dependencies (cached unless build files change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source and build
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S clinikai && adduser -S clinikai -G clinikai

# Copy jar from builder
COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown clinikai:clinikai app.jar

USER clinikai

EXPOSE 8080

# JVM tuning for containers
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
