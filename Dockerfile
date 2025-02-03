# Этап 1: Сборка приложения
FROM maven:3.9.6-eclipse-temurin-22 AS builder
WORKDIR /app_working_dir
COPY pom.xml .
COPY src ./src
RUN mvn clean install -Dmaven.compiler.release=22 -DskipTests

# Этап 2: Создание финального образа
FROM eclipse-temurin:22-jdk
WORKDIR /app_working_dir
COPY --from=builder /app_working_dir/target/moaishelper-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app_working_dir/src/main/resources
COPY ./src/main/resources/* /app_working_dir/src/main/resources/

CMD ["java", "-jar", "app.jar"]