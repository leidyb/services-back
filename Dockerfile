# Etapa 1: Construcción con Maven y JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B 
COPY src ./src
RUN mvn package -DskipTests -B

# Etapa 2: Ejecución con una JRE ligera
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]