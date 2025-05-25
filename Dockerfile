# Etapa 1: Construcción con Maven y JDK 21 (o la que uses)
# Usamos una imagen oficial de Maven que ya incluye la JDK.
# 'eclipse-temurin' es una distribución popular y bien mantenida de OpenJDK.
FROM maven:3.9-eclipse-temurin-21 AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar primero el pom.xml para aprovechar el caché de capas de Docker.
# Si el pom.xml no cambia, las dependencias no se descargarán de nuevo.
COPY pom.xml .

# Descargar todas las dependencias (modo offline para caché, -B para modo batch)
RUN mvn dependency:go-offline -B

# Copiar el resto del código fuente de tu aplicación
COPY src ./src

# Empaquetar la aplicación en un JAR ejecutable, saltando las pruebas para el build de Docker.
# -B es para modo batch, importante para entornos automatizados.
RUN mvn package -DskipTests -B

# Etapa 2: Ejecución con una JRE ligera (Java Runtime Environment)
# Usamos una imagen JRE que es más pequeña que una JDK completa.
FROM eclipse-temurin:21-jre-jammy

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR ejecutable construido en la etapa anterior (build) a esta etapa.
# El target/*.jar buscará cualquier archivo .jar en la carpeta target.
# Asegúrate que tu build de Maven produzca solo un JAR principal allí o ajusta el patrón.
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que tu aplicación Spring Boot escucha.
# Por defecto, Spring Boot usa 8080 si no se especifica server.port.
# Render utilizará esta información para mapear el puerto.
EXPOSE 8080

# Comando para ejecutar la aplicación cuando el contenedor inicie.
# Spring Boot usará application-prod.properties si la variable de entorno
# SPRING_PROFILES_ACTIVE=prod está configurada en Render.
ENTRYPOINT ["java", "-jar", "app.jar"]