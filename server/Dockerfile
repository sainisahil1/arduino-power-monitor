# Use an official OpenJDK image as the base image
FROM azul/zulu-openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the host machine to the container
COPY target/server-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (same as in application.properties)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
