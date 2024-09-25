FROM eclipse-temurin:17.0.11_9-jre-jammy
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]
EXPOSE 8090