FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/target/blogosphere-0.0.1-SNAPSHOT.jar app.jar

COPY --from=build /app/src/main/resources/templates /templates
COPY --from=build /app/src/main/resources/templates /static


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
