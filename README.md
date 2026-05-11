# Pet Management System

<br>

<div align="center">
  <a href="README.ru.md">🇷🇺 Русский</a> &nbsp;|&nbsp; <strong>🇺🇸 English</strong>
</div>

<br>

Pet Management System (cats and owners) – evolved from a simple CRUD Hibernate app into a microservice architecture with asynchronous Kafka communication and role‑based JWT authorization.

**Repository:** [https://github.com/capitlgdtt/Pet-Management-System](https://github.com/capitlgdtt/Pet-Management-System)

---

## 🚀 Technologies

- Java 21
- Spring Boot 3.4
- Spring Data JPA (Hibernate)
- Spring Security (JWT, roles)
- Apache Kafka
- PostgreSQL
- Flyway (migrations)
- Maven
- Lombok
- Swagger/OpenAPI (springdoc)
- JUnit, Mockito, MockMVC

---

## 🏗️ Architecture

The project consists of three microservices:

| Service          | Port  | Description                                                   |
|------------------|-------|---------------------------------------------------------------|
| **API Gateway**  | 8080  | Single entry point, JWT authentication, Swagger.             |
| **Cat Service**  | 8082  | Cat management (CRUD, filtering).                            |
| **Owner Service**| 8081  | Owner management (CRUD).                                     |

Communication between services – **synchronous request‑reply via Kafka** (topics `cats-*` and `owners-*`).  
Databases – separate schemas in PostgreSQL (single instance for simplicity).  
Migrations – Flyway.

---

## 🔐 Roles and Authorization

- **USER** – can view cats, create/edit/delete **their own** cats.
- **ADMIN** – full access to all cats and owners, can create/edit owners.

Authentication – JWT. After login, the client receives a token and sends it in the header `Authorization: Bearer <token>`.

---

## 📡 API Examples

> Base URL: `http://localhost:8080`

### Authentication

#### Register (creates a USER)
```bash
curl -X POST http://localhost:8080/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass"}'
```

#### Login
```bash
curl -X POST http://localhost:8080/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass"}'
# Response: {"token":"..."}
```

### Cats (token required)

#### Create a cat (USER – owner will be assigned automatically)
```bash
curl -X POST http://localhost:8080/cats \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Barsik","breed":"Siamese","color":"Ginger","tailLength":25}'
```

#### Get a cat by ID
```bash
curl http://localhost:8080/cats/1 -H "Authorization: Bearer <token>"
```

#### Get all cats filtered by color
```bash
curl "http://localhost:8080/cats/color?color=Ginger" -H "Authorization: Bearer <token>"
```

### Owners (ADMIN only)

#### Create an owner
```bash
curl -X POST http://localhost:8080/owners \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"newowner","password":"pass","role":"ROLE_USER"}'
```

#### Get an owner by ID
```bash
curl http://localhost:8080/owners/1 -H "Authorization: Bearer <admin_token>"
```

#### Update an owner (ADMIN only)
```bash
curl -X PUT http://localhost:8080/owners/1 \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"updatedname","role":"ROLE_USER"}'
```

## 🧪 Testing

- Unit tests for services (Mockito)
- Integration tests for controllers using MockMVC (with Kafka stubs)
- Run all tests: `mvn test`

---

## 📦 Local Setup

1. **Start infrastructure** (PostgreSQL, Kafka, ZooKeeper) using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   The `docker-compose.yml` file is in the project root.

2. **Build the project**:
   ```bash
   mvn clean package
   ```

3. **Run microservices (order does not matter)**:
   ```bash
   java -jar api-gateway/target/api-gateway-1.0-SNAPSHOT.jar
   java -jar cats/target/cats-1.0-SNAPSHOT.jar
   java -jar owners/target/owners-1.0-SNAPSHOT.jar
   ```

4. **Swagger UI**:
   > `http://localhost:8080/swagger-ui.html`

## 📌 Additional Features

- Pagination and filtering of cats by fields: name, breed, color, birth date, tail length.
- Adding friendship links between cats (`friendIds` list).
- Automatic creation of an admin on startup (login `base-admin` / password `base-password`).

---

## 🧑‍💻 Author

Vsevolod Grachev  
**GitHub:** [capitlgdtt](https://github.com/capitlgdtt)
