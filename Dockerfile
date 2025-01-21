FROM eclipse-temurin:latest

ARG JAR_PATH="build/libs"
ARG JAR_NAME="hotel-0.0.1-SNAPSHOT.jar"
ENV JAR_NAME_ENV=$JAR_NAME
ENV JAR_PATH_ENV=$JAR_PATH

WORKDIR /app

COPY $JAR_PATH/$JAR_NAME /app/$JAR_NAME

EXPOSE 5000

ENTRYPOINT ["sh", "-c", "java -jar /app/${JAR_NAME_ENV}"]
