FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8076
ENTRYPOINT ["java", "-jar", "app.jar"]

