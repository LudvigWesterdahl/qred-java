# payment

# 1. How to build and run

## 1.1. Create and start the database container (*optional)
```
./do dbcreate -p 8002 -w PASS
./do dbstart
```

This creates a database and user called `payment`.

## 1.2. Run - spring boot

### 1.2.1. Pre-conditions
Create an override config file with the db connection credentials `config/application-override.yml`.

For example if you run the container locally:
```
spring:
  datasource:
    url: "jdbc:postgresql://localhost:8002/payment"
    username: "payment"
    password: "PASSWORD"
```

### 1.2.2. Run
```
./run
```
Note that this also creates the database objects (tables, indexes, etc.) using liquibase automatically.

It will start running on port 8003 (hardcoded in application.yml).

## 1.3. Run - nodejs

### 1.3.1. Pre-conditions
You have performed the previous steps, that is, the database container and schema using liquibase (1.1. and 1.2.2.).

Create an environment file in the project root (qred-node):
```
DATABASE_URL=postgres://payment:PASSWORD@localhost:8002/payment
PORT=8004
```

### 1.3.2. Run
Install the dependencies locally:
```
npm install
```

Start the server (also runs the tests):
```
npm run dev
```

# 2. Documentation

## 2.1. Swagger - spring boot
http://localhost:8003/swagger-ui.html

## 2.2. Swagger - nodejs
http://localhost:8004/swagger-ui.html

## 2.3. Further reading
See `docs/`:
- `hl-architecture.d2`: overview of the system (see also the rendered txt file)

# 3. Development process
I create the java project by copying a lot of boilerplate from another projects which sped up that process. Since I'm comfortable working with liquibase
I introduced that to simplify creating the database objects. Grok was used to validate my implementation and help me identify bugs and minor boilerplate errors.

For the node part, since I don't have a complete setup for that on this machine and I have not had a lot of experience working with it, I relied on grok to create a first draft. 
I then went through the core domain logic of importing the payments to make sure it has been implemented accordingly. I gave it access to the java implementation for context
to make sure the API had the same error codes, for example. See `qred-node/AGENTS.md` file.


