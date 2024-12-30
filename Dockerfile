FROM eclipse-temurin:22-jdk
COPY ./target/app.jar app.jar

CMD ["java", "-jar", "app.jar"]