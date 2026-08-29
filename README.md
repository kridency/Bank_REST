# Banking card management system

## Description

Backend of banking card management application.

## Prerequisites

The runtime environment contains packages:

- Java SE 21 development and runtime platform;
- Docker containerization platform;
- Apache Maven build tool.

All commands are run in the terminal from the project folder ``Bank_REST/``.

No initial setup is required.

## Build package

The project is built using the shell command:
```
mvn clean package
```
Upon successful build, the results will be located in the folder ``target/``

## Run package

To run the package, execute the following commands in the project folder:
```
docker compose --project-directory ./docker/ up --detach
```
Upon successful зфслфпу startup, `docker` will run 2 containers `cards-microservice, postgres-container` in the `Up` status.

## Usage Instructions

The package functionality is accessed via the URL `http://localhost:8081/api/<endpoint>`, where `<endpoint>` represents a specific API resource.

A detailed API description is available at `http://localhost:8081/swagger-ui/index.html`.

The package uses `JWT`-based authentication. To obtain a token, send a request to `http://localhost:8081/api/login`. This authentication endpoint expects input data as a `JSON` object in the following format:
```
  { 
    "email": <адрес электронной почты>, 
    "password": <пароль> 
  }
 ``` 
Upon successful authentication, the `access_token` will be returned in the response body. Using this token, and based on the roles assigned during registration, the user will have access to the `users`, `cards`, and `transfers` resources.

### <span style="color: lightgreen">${users}$</span> resource commands

 - Вызов команды регистрации нового пользователя производится с помощью HTTP метода `POST`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
  { 
    "email": <адрес электронной почты>, 
    "password": <пароль> 
  }
 ```
Результатом успешного выполнения данной команды является создание учетной записи;
 
 - Вызов команды удаления пользователя производится с помощью HTTP метода `DELETE`. Результатом успешного выполнения данной команды является отсутствие учетной записи пользователя;
 
 - Вызов команды изменения записи пользователя производится с помощью HTTP метода `PUT`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
  { 
    "email": <адрес электронной почты>,
    "password": <пароль>,
    "roles": [роль1,...]
  }
 ```
 Результатом успешного выполнения данной команды является обновление учетной записи;
 
 - Вызов команды получения перечня пользователей производится с помощью HTTP метода `GET`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
  {
    "email": [адрес электронной почты]
  }
 ```
 - Результатом успешного выполнения данной команды является возврат перечня пользователей, удовлетворяющего критериям указанным в виде `JSON` нотации.

### Команды ресурса <span style="color: lightgreen">${cards}$</span>

 - Вызов команды создания банковской карты производится с помощью HTTP метода `POST`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации 
```
 {
   "pan": <#### #### ##### ####>,
   "expire_date": [yyyy-MM-dd],
   "email": <адрес электронной почты>,
   "status": [статус карты], 
   "balance": [остаток средств]
 }
```
Результатом успешного выполнения данной команды является создание записи банковской карты;

 - Вызов команды получения перечня банковских карт производится с помощью HTTP метода `GET`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
 { 
   "email": <адрес электронной почты>
 }
```
Результатом успешного выполнения данной команды является возврат перечня банковских карт, удовлетворяющих шаблону указанному в теле запроса.

 - Вызов команды обновления реквизитов банковской карты производится с помощью HTTP метода `PUT`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
 {
   "pan": <#### #### ##### ####>,
   "expire_date": [yyyy-MM-dd],
   "status": [статус карты],
   "balance": [остаток средств]
 }
```
 Результатом успешного выполнения данной команды является обновление записи банковской карты, исходя из существующего `Primary Account Number`;

- Вызов команды блокировки банковской карты производится с помощью HTTP метода `PATCH`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
 {
   "pan": <#### #### ##### ####>
 }
 ```
Результатом успешного выполнения данной команды является изменение статуса банковской карты, исходя из существующего `Primary Account Number`;

 - Вызов команды удаления банковской карты производится с помощью HTTP метода `DELETE`. Команда ожидает входные данные в виде объекта в следующей `JSON` нотации:
```
 {
   "pan": <#### #### ##### ####>
 }
 ```
 Результатом успешного выполнения данной команды является удаление записи банковской карты, исходя из существующего `Primary Account Number`.

### Команды ресурса <span style="color: lightgreen">${transfers}$</span>

- Вызов команды перевода средств производится с помощью HTTP метода `POST`. Команда ожидает входные данные в виде следующих параметров запроса:
```
origin = <#### #### #### ####>
destination = = <#### #### #### ####>
amount = <number>

```
Результатом успешного выполнения данной команды является создание записи транзакции и корректировка остатков средств на картах пользователя текущей сессии.