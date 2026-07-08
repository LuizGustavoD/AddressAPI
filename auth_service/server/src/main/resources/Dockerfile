FROM JDK:latest AS build

WORKDIR /app

COPY . .
RUN .mvnw clean package -DskipTests

FROM JDK:latest
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]