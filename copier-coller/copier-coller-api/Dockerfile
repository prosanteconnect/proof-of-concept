FROM maven:3-jdk-11 AS build
COPY copier-coller-api/src /usr/src/app/src
COPY copier-coller-api/pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM openjdk:11-slim-buster
COPY --from=build /usr/src/app/target/copier-coller-api*.jar /usr/app/copier-coller-api.jar
RUN mkdir -p /app/json-schemas-repo
RUN chown -R daemon: /app/json-schemas-repo
USER daemon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/copier-coller-api.jar"]
