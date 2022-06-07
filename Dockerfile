FROM openjdk:11-jre
WORKDIR /app
EXPOSE 8080
COPY target/lets-code-imdb-challenge.jar app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar