# ETAPA 1: Construcción (Build)
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom y descargamos dependencias (esto acelera futuros builds)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y generamos el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Runtime)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos solo el archivo JAR desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Creamos la carpeta para las fotos de las INEs para que no truene el sistema
RUN mkdir -p uploads/ines

# Exponemos el puerto 8080 (el que usa Spring Boot por defecto)
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]