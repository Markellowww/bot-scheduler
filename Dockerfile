# Этап 1: Сборка приложения
FROM maven:3.9.6-eclipse-temurin-22 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -Dmaven.compiler.release=22 -DskipTests

# Этап 2: Создание финального образа
FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY --from=builder /app/target/moaishelper-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/src/main/resources/ ./src/main/resources

CMD ["java", "-jar", "app.jar"]

