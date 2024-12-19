FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.repo.local=/root/.m2

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/target/Email-1.0-SNAPSHOT.jar app.jar
COPY src/main/resources /app/resources
ENTRYPOINT ["java", "-jar", "app.jar"]