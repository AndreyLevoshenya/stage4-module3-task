FROM openjdk:17-jdk-slim
WORKDIR /app
COPY module-web/build/libs/application-latest.jar application-latest.jar

ENV SERVER_ADDRESS=0.0.0.0
ENV SERVER_PORT=8443

EXPOSE 8443
ENTRYPOINT ["java", "-jar", "application-latest.jar"]