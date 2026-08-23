FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew clean bootJar --no-daemon

CMD ["java", "-jar", "build/libs/productiq-0.0.1-SNAPSHOT.jar"]