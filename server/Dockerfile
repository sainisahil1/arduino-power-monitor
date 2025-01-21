# First stage: build the application with Maven
FROM openjdk:17-jdk-slim AS build

# Install Maven manually
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Confirm Maven is available
RUN mvn -v

# Copy the entire project (including pom.xml)
COPY . .

# Build the Spring Boot app
RUN mvn clean package -DskipTests

# Second stage: use a lighter image to run the application
FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Command to run the app
CMD ["java", "-jar", "app.jar"]
