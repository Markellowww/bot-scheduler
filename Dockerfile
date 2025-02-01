FROM eclipse-temurin:22-jdk
COPY ./target/moaishelper-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]