FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
#ADD keystore.p12 keystore.p12
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080