FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY target/weatherapp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]