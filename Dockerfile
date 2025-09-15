# ---------- Build stage ----------
# Usa una imagen de Maven con JDK para compilar la aplicación
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia el archivo pom.xml y descarga las dependencias
# Esto permite aprovechar la caché de Docker si no cambian las dependencias
# y evita recompilar todo si solo cambian los archivos fuente
COPY pom.xml .
RUN mvn -q -e -DskipTests=true dependency:go-offline

# Copia el código y compila
COPY src ./src
# Construye el Jar sin ejecutar las pruebas
RUN mvn -q -e -DskipTests=true clean package

# ---------- Runtime stage ----------
# Usa una imagen de JRE para ejecutar la aplicación (más ligera)
FROM eclipse-temurin:21-jre-alpine
# Crea un usuario no root
# Esto es una buena práctica de seguridad para evitar correr la app como root
# y minimizar riesgos en caso de vulnerabilidades
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# Copia el jar desde la etapa de build
COPY --from=build /app/target/*.jar /app/app.jar

# Opciones JVM saneadas para contenedores
# Ajusta el uso de memoria y maneja errores de OOM
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck (necesitas spring-boot-starter-actuator en el pom)
# Comprueba que la app responde en /actuator/health y está UP cada 15s
# Espera hasta 3s por la respuesta y reintenta hasta 20 veces antes de marcar como fallo
# Esto ayuda a orquestadores como Docker Swarm o Kubernetes a gestionar la app
# y reiniciarla si no está saludable
HEALTHCHECK --interval=15s --timeout=3s --retries=20 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

# Puerto de la app
EXPOSE 8080
# Corre la app como el usuario no root
USER spring:spring
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
