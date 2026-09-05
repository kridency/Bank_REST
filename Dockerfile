# Базовый образ, содержащий Java 21
FROM alpine/java:21

# Директория приложения внутри контейнера
WORKDIR /app

# Определяем переменную сборки со значением по умолчанию
ARG SPRING_VERSION

# Копирование JAR-файла приложения в контейнер
COPY ./target/cards-microservice-1.0.0-SNAPSHOT.jar cards_microservice.jar

RUN mkdir -p /app/lib
COPY ./target/lib/ /app/lib/

ENV JAVA_TOOL_OPTIONS=-javaagent:/app/lib/spring-instrument-${SPRING_VERSION}.jar

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "cards_microservice.jar"]
