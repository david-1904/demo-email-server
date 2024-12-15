FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/email-server-app-0.0.1.jar email-server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "email-server.jar"]