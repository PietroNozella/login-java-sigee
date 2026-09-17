FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY target/login-sigee-java-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
