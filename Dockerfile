FROM eclipse-temurin:23-jdk

COPY target/app.jar /
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:7777,","app.jar"]