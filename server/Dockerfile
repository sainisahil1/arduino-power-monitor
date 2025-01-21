# Use an official OpenJDK image as the base image
FROM azul/zulu-openjdk:17 AS build


# Set the working directory inside the container
WORKDIR /app

# Copy project files to the container
COPY . .

# Build the application and create the JAR file
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file from the host machine to the container
COPY /app/target/*.jar app.jar

 # Expose the application port (same as in application.properties)
EXPOSE 8080

 # Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
