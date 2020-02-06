FROM maven:3-jdk-9-slim AS build-env
COPY src /app/src/
COPY pom.xml /app/
WORKDIR /app/
RUN mvn clean install -DskipTests
EXPOSE 8080

FROM gcr.io/distroless/java:11
WORKDIR /app
COPY --from=build-env /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]