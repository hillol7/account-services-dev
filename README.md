# account-services

## 1. About
A simple coding challenge to add, read and transfer money between accounts.

## 2. Dependencies

### 2.1. Application Runtime Dependencies
* [Java Runtime Environment 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (Oracle preferred).
* [Swagger](https://swagger.io/)
* [Lombok](https://projectlombok.org/)
* [Spring Devtools](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools/1.3.0.RELEASE)
* [Spring Actuator](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator)
### 2.2. Service Dependencies
NA

## 3. System Requirements
* Minimum of 256MB RAM assigned to the JVM process.

## 4. Usage

###  HTTP REST API Usage - accounts

###  Request / Response
***Get End point*** 
  
 | What | Value |
| ---- | ----- |
| Verb | `GET` |
| URI | `http://<host>:<port>/v1/accounts/<accountId>` |
| Header | `Accepts: application/json` |  

| URI | Description |
| ---------------- | ----------- |
| `accountId` | Account ID is of type String, comprising alphanumeric characters. |

***Request Payload***
ID-1

###  Sample Response
***This will show up in tandem with either a `200` HTTP Status Code.***  
```json
{
  "data": {
    "accountId": "string",
    "balance": 0
  },
  "error": "string",
  "guid": "string"
}
```
***Sample Error Response - 404 Bad Request***   
```json
{
 {
    "uuid": "2f315e99-9081-4fec-b08c-7f890c8523e4",
    "data": null,
    "errors": null
}
}

```
***POST End point*** 
***Request***

| What | Value |
| ---- | ----- | 
| Endpoint | `http://<host>:<port>/v1/accounts` |
| Verb | `POST` |

***Request Payload***
```json
{
  "accountId": "string",
  "balance": 0
}
```

***Response Payload***   
```json
{
  "data": {
    "accountId": "string",
    "balance": 0
  },
  "error": "string",
  "guid": "string"
}
```
***PUT End point***   

| What | Value |
| ---- | ----- | 
| Endpoint | `http://<host>:<port>/v1/accounts/transfer` |
| Verb | `PUT` |
***Request Payload***
```json
{
  "fromAccountId": "string",
  "toAccountId": "string",
  "amount": 0
}
```
***Response Payload***   
```json
{
  "data": "string",
  "error": "string",
  "guid": "string"
}
```
## 4.1 Building and running the application
Go to the project root dir and execute the below:
```
gradle clean compileJava build jacocoTestReport fatJar
```

After a successful build go to the `build` dir and then cd into the `libs`:
Run the following command:
```
java -jar account-services-0.0.1-SNAPSHOT.jar
```
## 4.2 Checking the code coverage
After the `step` `4.1` , go to the `/reports/coverage/index.html` to see the code coverage.

## 4.3 Swagger Documentation
After running the application please open the below url to check the swagger ui, this can be used to test the app and see the documentatin.

```
http://localhost:18080/swagger-ui.html#
```
Check the `accounts-controller : Accounts Controller` section for APIs.
## 4.4 Application health, metrics, env etc.
```
http://localhost:18080/loggers
http://localhost:18080/heapdump?live=true
http://localhost:18080/health
http://localhost:18080/env
http://localhost:18080/mappings
http://localhost:18080/dump
http://localhost:18080/metrics
```
More information available at:
```
http://localhost:18080/swagger-ui.html#
```
## 5 Configuration<a name="configuration"></a>
```yml
NA
### 5.1. Global Configuration<a name="configuration_global"></a>
#### 5.1.1. Example<a name="configuration_global_example"></a>
```yaml
server:
    port: 18080
management:
    security:
        enabled: false
```
## 5.2. App Specific Configuration<a name="configuration_appspecific"></a>
```yml
NA
```
#### 5.2.1. Example<a name="configuration_appspecific_example"></a>
```yml
NA
```
#### 5.2.2. Description<a name="configuration_appspecific_description"></a>
```yml
NA
```
## 6. Database<a name="database"></a>
```sql
NA
```
### 6.1. DDL<a name="database_ddl"></a>
``` sql
NA
```
***EOF***   

