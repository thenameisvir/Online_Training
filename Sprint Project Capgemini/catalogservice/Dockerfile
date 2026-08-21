# 1. Java ka base image (Platform)
FROM eclipse-temurin:21-jdk-alpine

# 2. Container ke andar ka working folder
WORKDIR /app

# 3. JAR file ko copy karke container mein 'app.jar' naam dena
COPY target/*.jar app.jar

# 4. Command jo application ko start karegi
ENTRYPOINT ["java", "-jar", "app.jar"]