# Java ka base image (Platform)
FROM eclipse-temurin:21-jdk-alpine

# Container ke andar ka working folder
WORKDIR /app

# JAR file ko copy karke container mein 'app.jar' naam dena
# Make sure to run 'mvn clean package' before building
COPY target/*.jar app.jar

# Command jo application ko start karegi
ENTRYPOINT ["java", "-jar", "app.jar"]
