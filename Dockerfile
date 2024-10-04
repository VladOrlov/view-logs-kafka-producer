# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file to the container
COPY target/view-logs-kafka-producer-0.1.0.jar /app/view-logs-kafka-producer-0.1.0.jar

# Expose the default port Spring Boot runs on
EXPOSE 8080

# Set an environment variable to pass Spring profile (optional)
ENV SPRING_PROFILES_ACTIVE=default

# Allow Kafka bootstrap servers to be passed as an environment variable
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
#ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092

# Add a default entrypoint to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/view-logs-kafka-producer-0.1.0.jar"]

# Allow passing additional arguments to the Java command (like profiles)
CMD ["--spring.profiles.active=${SPRING_PROFILES_ACTIVE}", "--spring.kafka.bootstrap-servers=${SPRING_KAFKA_BOOTSTRAP_SERVERS}"]
