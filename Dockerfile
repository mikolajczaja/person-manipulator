FROM eclipse-temurin:23-jdk

COPY target/app.jar /
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]