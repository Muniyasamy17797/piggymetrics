# Use official Maven image as builder
FROM maven:3.8.6-openjdk-8 AS builder
WORKDIR /app

# Copy the entire PiggyMetrics project (including parent POM)
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use lightweight runtime image
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=builder /app/account-service/target/account-service.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
