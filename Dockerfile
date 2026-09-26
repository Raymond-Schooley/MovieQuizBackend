# Production image: compiles the jar in a throwaway build stage, then
# copies only the jar into a slim JRE runtime image.

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

# Resolve dependencies first so this layer is cached across builds that
# only change application source, not pom.xml.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S moviequiz && adduser -S moviequiz -G moviequiz
WORKDIR /app
COPY --from=build /build/target/movie-quiz-backend.jar app.jar
USER moviequiz

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
