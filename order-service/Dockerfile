# Build stage: compile and package the service with Maven
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
COPY order-service/pom.xml order-service/
RUN mvn -B -q -pl order-service dependency:go-offline
COPY order-service/src order-service/src
RUN mvn -B -q -pl order-service package -DskipTests

# Runtime stage: JRE only, non-root user
FROM eclipse-temurin:25-jre
RUN useradd --system --uid 1001 app
USER app
WORKDIR /app
COPY --from=build /workspace/order-service/target/order-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
