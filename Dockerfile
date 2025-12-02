FROM eclipse-temurin:21-jre as runtime

ARG JAR_FILE=target/recommendation-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]