FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/email-server-app-0.0.1.jar email-server.jar

EXPOSE 8080
EXPOSE 5006

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006", "-jar", "email-server.jar"]
