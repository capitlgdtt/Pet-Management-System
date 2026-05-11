# Pet Management System

Система управления питомцами (котиками) и их владельцами. Проект эволюционировал от простого CRUD‑приложения на Hibernate до микросервисной архитектуры с асинхронным общением через Kafka и ролевой авторизацией (JWT).

**Репозиторий:** [https://github.com/capitlgdtt/Pet-Management-System](https://github.com/capitlgdtt/Pet-Management-System)

---

## 🚀 Технологии

- Java 21
- Spring Boot 3.4
- Spring Data JPA (Hibernate)
- Spring Security (JWT, роли)
- Apache Kafka
- PostgreSQL
- Flyway (миграции)
- Maven
- Lombok
- Swagger/OpenAPI (springdoc)
- JUnit, Mockito, MockMVC

---

## 🏗️ Архитектура

Проект состоит из трёх микросервисов:

| Сервис          | Порт  | Описание                                          |
|----------------|-------|---------------------------------------------------|
| **API Gateway** | 8080  | Единая точка входа, JWT‑аутентификация, Swagger. |
| **Cat Service** | 8082  | Управление котиками (CRUD, фильтрация).          |
| **Owner Service** | 8081 | Управление владельцами (CRUD).                   |

Общение между сервисами – **синхронный запрос‑ответ через Kafka** (топики `cats-*` и `owners-*`).  
Базы данных – отдельные схемы в PostgreSQL (единый экземпляр для простоты).  
Миграции – Flyway.

---

## 🔐 Роли и авторизация

- **USER** – может просматривать котиков, создавать/редактировать/удалять **своих** котиков.
- **ADMIN** – полный доступ ко всем котикам и владельцам, может создавать/изменять владельцев.

Аутентификация – JWT. После логина клиент получает токен и передаёт его в заголовке `Authorization: Bearer <token>`.

---

## 📡 API (примеры)

> Базовый URL: `http://localhost:8080`

### Аутентификация

#### Регистрация (создаётся USER)
```bash
curl -X POST http://localhost:8080/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass"}'
```


#### Логин
```bash
curl -X POST http://localhost:8080/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass"}'
# Ответ: {"token":"..."}
```

#### Котики (требуется токен)

###### Создать котика (USER – автоматически подставится владелец)
```bash
curl -X POST http://localhost:8080/cats \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Барсик","breed":"Сиамский","color":"Рыжий","tailLength":25}'
```

###### Получить котика по ID
```bash
curl http://localhost:8080/cats/1 -H "Authorization: Bearer <token>"
```

###### Получить всех котиков с фильтрацией по цвету
```bash
curl "http://localhost:8080/cats/color?color=Рыжий" -H "Authorization: Bearer <token>"

```

#### Владельцы (только ADMIN)

###### Создать владельца
```bash
curl -X POST http://localhost:8080/owners \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"newowner","password":"pass","role":"ROLE_USER"}'
```

###### Получить владельца по ID
```bash
curl http://localhost:8080/owners/1 -H "Authorization: Bearer <admin_token>"
```

###### Обновить владельца (только ADMIN)
```bash
curl -X PUT http://localhost:8080/owners/1 \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"updatedname","role":"ROLE_USER"}'
```

## 🧪 Тестирование

- Unit‑тесты сервисов (Mockito)
- Интеграционные тесты контроллеров через MockMVC (с заглушками Kafka)
- Для запуска всех тестов: `mvn test`

---

## 📦 Запуск локально

1. **Запустить инфраструктуру** (PostgreSQL, Kafka, ZooKeeper) через Docker Compose:
   ```bash
   docker-compose up -d
   ```
   Файл docker-compose.yml в корне проекта.

2. **Собрать проект**:
   ```bash
   mvn clean package
   ```
3. **Запустить микросервисы (порядок не важен)**:
   ```bash
   java -jar api-gateway/target/api-gateway-1.0-SNAPSHOT.jar
   java -jar cats/target/cats-1.0-SNAPSHOT.jar
   java -jar owners/target/owners-1.0-SNAPSHOT.jar
   ```
4. **Swagger UI**:
   > `http://localhost:8080/swagger-ui.html`

## 📌 Дополнительные возможности

- Пагинация и фильтрация котиков по полям: имя, порода, цвет, дата рождения, длина хвоста.
- Добавление дружеских связей между котиками (список `friendIds`).
- Автоматическое создание администратора при старте (логин `base-admin` / пароль `base-password`).

---

## 🧑‍💻 Автор

Грачев Всеволод  
**GitHub:** [capitlgdtt](https://github.com/capitlgdtt)

---
