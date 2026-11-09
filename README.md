# payment

# 1. How to build

## 1.1. Create and start the database container
```
./do dbcreate -p 8002 -w PASS
./do dbstart
```

## 1.2. Run - spring boot

### 1.2.1. Pre-conditions
Create an override config file with the db connection credentials `config/application-override.yml`.
```
spring:
  datasource:
    url: "jdbc:postgresql://localhost:8002/payment"
    username: "payment"
    password: "PASSWORD"
```

### 1.2.2. Run
This also creates the schema using liquibase automatically.
```
./run
```

It will start running on port 8003 (hardcoded in application.yml).

## 1.3. Run - nodejs

### 1.3.1. Pre-conditions
You have performed the previous steps, that is, the database container and schema using liquibase (1.1. and 1.2.2.).

### 1.3.2. Run
TODO


# 2. Documentation

## 2.1. Swagger - spring boot
http://localhost:8003/swagger-ui.html

## 2.2. Swagger - nodejs
TODO

