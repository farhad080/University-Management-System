# Stage 1: Build the application using Eclipse Temurin JDK and Maven Wrapper
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# Copy Maven wrapper configuration and dependencies descriptor
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Copy source code and build the executable JAR
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal JRE runtime environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root application user for container security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy compiled JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose application port
EXPOSE 8088

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
