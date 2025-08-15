# Базовый образ, содержащий Java 21
FROM openjdk:17-oracle

#Директория приложения внутри контейнера
WORKDIR /app

ENV POSTGRES_DATASOURCE_URL=jdbc:postgresql://localhost:5432/card_db

EXPOSE 8080

#Копирование JAP-файла приложения в контейнер
COPY ./target/cards-microservice-1.0.0-SNAPSHOT-jar-with-dependencies.jar cards_microservice.jar

#Команда для запуска приложения
ENTRYPOINT ["java", "-cp", "cards_microservice.jar", "com.example.bankcards.BankCardsApplication"]
