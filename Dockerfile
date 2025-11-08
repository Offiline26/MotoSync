# 1ª fase: build da aplicação
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /usr/app

COPY . .
RUN gradle clean build -x test

# 2ª fase: imagem leve só com o JAR
FROM eclipse-temurin:21-jre-alpine

WORKDIR /usr/app

# copie o JAR gerado (ajuste o nome se for diferente)
COPY --from=build /usr/app/build/libs/api-security-0.0.1-SNAPSHOT.jar app.jar

# Render usa a variável PORT, então não fixe porta aqui,
# só exponha uma "default" qualquer (não é obrigatório, mas ajuda)
EXPOSE 8080

# se quiser passar JAVA_OPTS depois
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
