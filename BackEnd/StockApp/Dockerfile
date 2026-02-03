FROM maven:3.9.11-eclipse-temurin-25-noble  AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM docker.io/eclipse-temurin:25-jre-noble
WORKDIR /app
COPY --from=build /app/target/*.jar stockApp-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "stockApp-0.0.1-SNAPSHOT.jar"]