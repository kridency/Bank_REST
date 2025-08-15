# Базовый образ, содержащий Java 21
FROM openjdk:17-oracle

#Директория приложения внутри контейнера
WORKDIR /app

ENV POSTGRES_DATASOURCE_URL=jdbc:postgresql://localhost:5432/card_db
ENV JAVA_TOOL_OPTIONS=-javaagent:/app/lib/spring-instrument-6.1.9.jar

EXPOSE 8080

#Копирование JAP-файла приложения в контейнер
COPY ./target/cards-microservice-1.0.0-SNAPSHOT.jar cards_microservice.jar
RUN mkdir -p /app/lib
COPY ./target/lib/ /app/lib/

#Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "cards_microservice.jar"]
