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
    "email": <email address>, 
    "password": <password> 
  }
 ``` 
Upon successful authentication, the `access_token` will be returned in the response body. Using this token, and based on the roles assigned during registration, the user will have access to the `users`, `cards`, and `transfers` resources.

### <span style="color: lightgreen">${users}$</span> resource commands

 - The command to register a new user is invoked using the HTTP `POST` method. The command expects input data as an object in the following `JSON` notation:
```
  { 
    "email": <email address>, 
    "password": <password>,
    "roles": [role1,...] 
  }
 ```
Successful execution of this command results in the creation of a user account.

The command to delete a user is invoked using the HTTP `DELETE` method. The command expects input data as an object in the following `JSON` notation:
```
 { 
   "email": <email address>
 }
```
Successful execution of this command ensures that the user account no longer exists.
 
 - The command to update a user record is invoked using the HTTP `PUT` method. The command expects input data as an object in the following `JSON` notation:
```
  { 
    "email": <адрес электронной почты>,
    "password": <пароль>,
    "roles": [роль1,...]
  }
 ```
Successful execution of this command results in the update of the user account.
 
 - The command to retrieve a list of users is invoked using the HTTP `GET` method. The command expects input data as an object in the following `JSON` notation:
```
  {
    "email": [email address]
  }
 ```
Successful execution of this command returns a list of users that matches the criteria specified in the request body.

### <span style="color: lightgreen">${cards}$</span> resource commands

 - The command to create a bank card is invoked using the HTTP `POST` method. The command expects input data as an object in the following `JSON` notation: 
```
 {
   "pan": <#### #### ##### ####>,
   "expire_date": [yyyy-MM-dd],
   "email": <email address>,
   "status": [card status], 
   "balance": [card balance]
 }
```
Successful execution of this command results in the creation of a bank card record.

 - The command to retrieve a list of bank cards is invoked using the HTTP `GET` method. The command expects input data as an object in the following `JSON` notation:
```
 { 
   "email": <email address>
 }
```
Successful execution of this command returns a list of bank cards that matches the template specified in the request body.

 - The command to update bank card details is invoked using the HTTP `PUT` method. The command expects input data as an object in the following `JSON` notation:
```
 {
   "pan": <#### #### ##### ####>,
   "expire_date": [yyyy-MM-dd],
   "status": [card status],
   "balance": [card balance]
 }
```
Successful execution of this command results in the update of the bank card record based on the existing `Primary Account Number`.

- The command to block a bank card is invoked using the HTTP `PATCH` method. The command expects input data as an object in the following `JSON` notation:
```
 {
   "pan": <#### #### ##### ####>
 }
 ```
Successful execution of this command results in a change to the bank card status based on the existing `Primary Account Number`.

 - The command to delete a bank card is invoked using the HTTP `DELETE` method. The command expects input data as an object in the following `JSON` notation:
```
 {
   "pan": <#### #### ##### ####>
 }
 ```
Successful execution of this command results in the deletion of the bank card record based on the existing `Primary Account Number`.

### <span style="color: lightgreen">${transfers}$</span> resource commands

- The command to transfer funds is invoked using the HTTP `POST` method. The command expects input data as the following request parameters:
```
origin = <#### #### #### ####>
destination = = <#### #### #### ####>
amount = <number>

```
Successful execution of this command results in the creation of a transaction record and the adjustment of card balances for the current session user.