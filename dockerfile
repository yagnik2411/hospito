# --- STAGE 1: THE BUILDER (Heavy Image) ---
FROM maven:3.8.5-openjdk-17 AS builder
# Starts the first stage named "builder". 
# Uses a large image containing Maven and JDK tools to compile your code.

WORKDIR /app
# Creates a folder named "/app" inside the container and goes inside it.

COPY pom.xml .
# Copies your local 'pom.xml' file into the container's '/app' folder.

RUN mvn dependency:go-offline
# Downloads all your dependencies (libraries) from the internet. 
# We do this BEFORE copying source code so Docker can cache (save) this step.

COPY src ./src
# Copies your actual Java source code folders into the container.

RUN mvn clean package -DskipTests
# Compiles your code and turns it into a .jar file inside the 'target' folder. 
# '-DskipTests' prevents running unit tests to save build time.


# --- STAGE 2: THE RUNNER (Lightweight Image) ---
FROM eclipse-temurin:17-jre
# Discards the previous heavy image. Starts a new, tiny image containing ONLY Java Runtime.

WORKDIR /app
# Sets the working directory in this new clean image.

COPY --from=builder /app/target/*.jar app.jar
# The Magic Step: It reaches back into the "builder" stage, grabs ONLY the .jar file 
# created there, and copies it here as "app.jar".

ENTRYPOINT ["java", "-jar", "app.jar"]
# The command that runs automatically when you start the container.