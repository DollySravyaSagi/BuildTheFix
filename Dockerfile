# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and package jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create minimal runtime container
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/buildthefix-1.0.0.jar app.jar

# Expose default port
EXPOSE 8080

# Configure start command with Render PORT variable support
ENV PORT=8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
