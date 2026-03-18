# Tic-Tac-Toe Game Server

Серверное приложение для игры в крестики-нолики с поддержкой многопользовательского режима, авторизации и игры против компьютера.

---

## 📋 Оглавление

- [Технологический стек](#-технологический-стек)
- [Архитектура](#-архитектура)
- [Быстрый старт](#-быстрый-старт)
- [Конфигурация](#️-конфигурация)
- [API Endpoints](#-api-endpoints)
- [Структура проекта](#-структура-проекта)
- [Разработка](#-разработка)

---

## Технологический стек

| Технология          | Версия  | Назначение                     |
| ------------------- | ------- | ------------------------------ |
| **Java**            | 18      | Основной язык программирования |
| **Spring Boot**     | 4.0.2   | Фреймворк для веб-приложений   |
| **Spring Security** | -       | Авторизация и аутентификация   |
| **Spring Data JPA** | -       | Работа с базой данных          |
| **PostgreSQL**      | 15      | Реляционная БД                 |
| **Lombok**          | 1.18.42 | Сокращение boilerplate кода    |
| **Gradle**          | 8.14.0  | Система сборки                 |
| **Docker**          | -       | Контейнеризация                |

---

## Архитектура

Проект реализован на основе **Hexagonal Architecture** (архитектура портов и адаптеров).

### Принципы гексагональной архитектуры

![Многослойная архитектура](../../misc/images/onion.png)

### Структура слоев

#### **Domain Layer** (Ядро) 🔷

**Расположение:** `by.michael.noughtsandcrosses.domain`

Центральный слой, содержащий бизнес-логику:

- **model/** - доменные сущности (Game, User, Sign)
- **service/** - интерфейсы и реализации бизнес-сервисов
- **algorithm/** - алгоритм проверки победителя в игре
- **exception/** - доменные исключения

**Особенности:**

- Не зависит от фреймворков (Spring, Hibernate)
- Не знает о БД и веб-технологиях
- Содержит чистую бизнес-логику
- Легко тестируется

#### 2️⃣ **Web Layer** (Входной адаптер)

**Расположение:** `by.michael.noughtsandcrosses.web`

Адаптер для взаимодействия с внешним миром через HTTP:

- **controller/** - REST контроллеры
- **filter/** - фильтры безопасности (AuthFilter)
- **config/** - конфигурация Spring Security
- **mapper/** - преобразование DTO ↔ Domain models
- **model/** - DTO для запросов и ответов
- **exception/** - обработка исключений

**Ответственность:**

- Принимает HTTP запросы
- Преобразует DTO в доменные модели
- Вызывает сервисы из domain
- Возвращает HTTP ответы

#### 3️⃣ **Datasource Layer** (Выходной адаптер)

**Расположение:** `by.michael.noughtsandcrosses.datasource`

Адаптер для работы с PostgreSQL:

- **repository/** - Spring Data JPA репозитории
- **model/** - JPA entity (с аннотациями @Entity)
- **mapper/** - преобразование Entity ↔ Domain models
- **exception/** - исключения уровня данных

**Ответственность:**

- Сохранение данных в PostgreSQL
- Извлечение данных из БД
- Преобразование JPA Entity в доменные модели

#### 4️⃣ **DI Layer** (Dependency Injection) ⚙️

**Расположение:** `by.michael.noughtsandcrosses.di`

Конфигурация Spring для связывания слоев:

- **SpringConfiguration** - настройка бинов и зависимостей

### Преимущества такой архитектуры

| Преимущество      | Описание                                |
| ----------------- | --------------------------------------- |
| **Тестируемость** | Domain core тестируется без Spring/БД   |
| **Гибкость**      | Легко заменить PostgreSQL на MongoDB    |
| **Изоляция**      | Изменения в БД не влияют на domain      |
| **Независимость** | Бизнес-логика не зависит от фреймворков |
| **Чистота**       | Четкое разделение ответственности       |

---

## Быстрый старт

### Требования

- **Docker** и **Docker Compose**
- Свободный порт **8080** (приложение)
- Свободный порт **5432** (PostgreSQL)

### Запуск приложения

1. Клонируйте репозиторий и перейдите в директорию проекта:

```bash
cd src/tic-tac-toe
```

2. Запустите приложение через Docker Compose:

```bash
docker-compose up --build
```

3. Дождитесь сообщения о запуске:

```
tic-tac-toe-app | Started Runner in X.XXX seconds
```

4. Приложение доступно по адресу:

```
http://localhost:8080
```

### Остановка приложения

```bash
docker-compose down
```

Для удаления данных из базы:

```bash
docker-compose down -v
```

---

## Конфигурация

### Docker Compose

Файл `docker-compose.yml` настраивает два сервиса:

#### 🐘 PostgreSQL

- **Порт:** 5432
- **База данных:** s21_tic_tac_toe
- **Пользователь:** postgres
- **Пароль:** postgres
- **Healthcheck:** Проверка готовности перед запуском приложения

#### ☕ Spring Boot Application

- **Порт:** 8080
- **Зависит от:** PostgreSQL (ждет healthcheck)
- **Автоматическая настройка:** Переменные окружения для подключения к БД

### Application Properties

Конфигурация Spring Boot в `application.properties`:

```properties
# БД настраивается через переменные окружения в docker-compose.yml
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

---

## API Endpoints

### 🔐 Авторизация

#### Регистрация пользователя

```http
POST /auth/signup
Content-Type: application/json

{
  "login": "user123",
  "password": "secretPassword"
}
```

**Ответ:** `200 OK` - успешная регистрация

#### Авторизация

```http
POST /auth/login
Authorization: Basic base64(login:password)
```

**Ответ:** UUID пользователя

---

### 🎮 Игра

> ⚠️ Все endpoints требуют авторизацию (кроме `/auth/*`)

#### Создать новую игру

```http
POST /game/new
Authorization: Basic base64(login:password)
Content-Type: application/json

{
  "opponentType": "COMPUTER" | "PLAYER"
}
```

#### Получить список доступных игр

```http
GET /game/available
Authorization: Basic base64(login:password)
```

#### Присоединиться к игре

```http
POST /game/{gameId}/join
Authorization: Basic base64(login:password)
```

#### Сделать ход

```http
PUT /game/{gameId}/move
Authorization: Basic base64(login:password)
Content-Type: application/json

{
  "position": 0-8
}
```

#### Получить состояние игры

```http
GET /game/{gameId}
Authorization: Basic base64(login:password)
```

---

### Пользователи

#### Получить информацию о пользователе

```http
GET /user/{userId}
Authorization: Basic base64(login:password)
```

---

## 📂 Структура проекта

```
src/tic-tac-toe/
├── 📄 docker-compose.yml          # Оркестрация контейнеров
├── 📄 Dockerfile                  # Multi-stage сборка образа
├── 📄 build.gradle.kts            # Конфигурация Gradle
├── 📄 settings.gradle.kts
└── src/main/
    ├── java/by/michael/noughtsandcrosses/
    │   ├── 🚀 Runner.java         # Точка входа
    │   │
    │   ├── 🔷 domain/             # CORE - Бизнес-логика
    │   │   ├── model/             # Доменные модели
    │   │   │   ├── Game.java
    │   │   │   ├── User.java
    │   │   │   ├── Sign.java
    │   │   │   └── GameStatus.java
    │   │   ├── service/           # Интерфейсы и реализации
    │   │   │   ├── GameService.java
    │   │   │   ├── GameServiceImpl.java
    │   │   │   ├── AuthService.java
    │   │   │   ├── AuthServiceImpl.java
    │   │   │   ├── UserService.java
    │   │   │   └── UserServiceImpl.java
    │   │   ├── algorithm/         # Алгоритм игры
    │   │   │   └── WinChecker.java
    │   │   └── exception/         # Доменные исключения
    │   │
    │   ├── 🌐 web/                # Входной адаптер (HTTP)
    │   │   ├── controller/        # REST контроллеры
    │   │   │   ├── AuthController.java
    │   │   │   ├── GameController.java
    │   │   │   ├── UserController.java
    │   │   │   └── WebController.java
    │   │   ├── filter/            # Security фильтры
    │   │   │   └── AuthFilter.java
    │   │   ├── config/            # Spring Security конфиг
    │   │   │   └── SecurityConfig.java
    │   │   ├── mapper/            # DTO ↔ Domain
    │   │   ├── model/             # DTO классы
    │   │   └── exception/         # Обработка ошибок
    │   │
    │   ├── 💾 datasource/         # Выходной адаптер (БД)
    │   │   ├── repository/        # Spring Data JPA
    │   │   │   ├── GameRepository.java
    │   │   │   └── UserRepository.java
    │   │   ├── model/             # JPA Entity
    │   │   ├── mapper/            # Entity ↔ Domain
    │   │   └── exception/         # Исключения БД
    │   │
    │   └── ⚙️ di/                 # Dependency Injection
    │       └── SpringConfiguration.java
    │
    └── resources/
        ├── application.properties  # Конфигурация Spring
        ├── static/                # JS фронтенд
        │   └── js/
        │       ├── app.js
        │       └── profile.js
        └── templates/             # HTML шаблоны
            ├── index.html
            └── profile.html
```

---

## Разработка

### Локальная разработка без Docker

1. Установите PostgreSQL и создайте базу данных:

```sql
CREATE DATABASE s21_tic_tac_toe;
```

2. Настройте `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/s21_tic_tac_toe
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Запустите приложение:

```bash
./gradlew bootRun
```

### Запуск тестов

```bash
./gradlew test
```

### Сборка JAR

```bash
./gradlew bootJar
```

Артефакт будет в `build/libs/`.

---

## 🤝 Контакты

Если возникли вопросы или нашли баг - создайте issue в репозитории.

---

**Приятной игры! 🎮**
