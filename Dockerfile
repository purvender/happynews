# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY happynews.jar app.jar

# Expose port 8080 (or the port your app listens on)
EXPOSE 8080

# Define environment variables if needed (optional)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
