FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/email-server-app-0.0.1.jar email-server.jar

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "email-server.jar"]
