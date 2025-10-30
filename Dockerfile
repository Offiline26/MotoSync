FROM gradle:jdk21-graal AS BUILD
WORKDIR /usr/app
COPY . .
RUN ./gradlew clean bootJar -x test --no-daemon

FROM openjdk:21-jdk-slim
COPY --from=BUILD /usr/app .
EXPOSE 8081
ENTRYPOINT ["java","-jar","build/libs/api-security-0.0.1-SNAPSHOT.jar"]