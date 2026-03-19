# Project Backend 04 — Java Bootcamp

<div align="center">

### Крестики-Нолики: База данных + Авторизация

**Научись добавлять базы данных в веб-приложения на Java с использованием Spring и работать с авторизацией**

[![Java](https://img.shields.io/badge/Java-18-orange?style=flat&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?style=flat&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat&logo=postgresql)](https://www.postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat&logo=docker)](https://www.docker.com)

</div>

---
![1080](https://github.com/user-attachments/assets/6aafb85a-bb43-4018-bf10-7dd74a082605)

<div align="center">

### Запуск

```bash
cd src/tic-tac-toe
docker-compose up --build
```

**Приложение будет доступно на** `http://localhost:8080`

---

</div>

## Проект

![Главное меню](misc/images/lobby.png)
![Игра ждет вашего хода](misc/images/yourturn.png)
![Победил Х](misc/images/waitingsecondplayer.png)
![Страница авторизации](misc/images/authform.png)
![Профиль](misc/images/profile.png)

## 📋 Содержание

<table>
<tr>
<td width="33%" valign="top">

### 📖 Теория

- [Общая информация](#chapter-ii-общая-информация)
  - [Авторизация](#авторизация)
  - [Идентификация vs Аутентификация](#идентификация-аутентификация-авторизация)
  - [Basic Authentication](#авторизация-с-помощью-логина-и-пароля)

</td>
<td width="33%" valign="top">

### 🎯 Практика

- [Задание 1](#задание-1-добавление-базы-данных): База данных
- [Задание 2](#задание-2-добавление-авторизации): Авторизация
- [Задание 3](#задание-3-добавление-логики-игры-между-двумя-игроками): Многопользовательская игра

</td>
<td width="33%" valign="top">

### 🛠 Технологии

- Spring Data JPA
- Spring Security
- PostgreSQL
- Docker Compose
- RESTful API
- Lombok

</td>
</tr>
</table>

---

## Chapter II: Общая информация

<div align="center">
<h3>🔐 Основы безопасности веб-приложений</h3>
</div>

---

### 🛡 Авторизация

**Авторизация** — средства контроля доступа легальных пользователей к ресурсам системы, предоставляющие каждому из них именно те права, которые были определены администратором.

---

### 🔑 Идентификация, Аутентификация, Авторизация

Три ключевых понятия безопасности:

<table>
<tr>
<th width="25%">Термин</th>
<th width="30%">Определение</th>
<th width="25%">Пример</th>
<th width="20%">Вопрос</th>
</tr>

<tr>
<td><strong>🆔 Идентификация</strong></td>
<td>Процедура, в результате выполнения которой для субъекта выявляется его <strong>уникальный признак</strong>, однозначно определяющий его в информационной системе.</td>
<td>Ввод логина: <code>user123</code></td>
<td><em>"Кто вы?"</em></td>
</tr>

<tr>
<td><strong>🔐 Аутентификация</strong></td>
<td>Процедура <strong>проверки подлинности</strong>, например, проверка подлинности пользователя путем сравнения введенного им пароля с паролем, сохраненным в системе.</td>
<td>Проверка пароля: <code>secret123</code><br>✅ Совпадает с сохраненным</td>
<td><em>"Вы это действительно вы?"</em></td>
</tr>

<tr>
<td><strong>✅ Авторизация</strong></td>
<td>Предоставление определенному лицу или группе лиц <strong>прав на выполнение</strong> определенного набора действий.</td>
<td>Пользователь может:<br>• Создавать игры<br>• Делать ходы<br>❌ Не может смотреть игры других</td>
<td><em>"Что вам разрешено делать?"</em></td>
</tr>
</table>

---

### 🔒 Авторизация с помощью логина и пароля

**Basic Authentication** — метод HTTP-аутентификации, основанный на передаче логина и пароля в заголовке запроса.

#### 🔄 Процесс работы

<div align="center">
  <img src="misc/images/Auth.png" alt="Basic Authentication Flow"/>
</div>

#### 📝 Алгоритм

```
1. Клиент → Сервер: Запрос без авторизации
   GET /api/game

2. Сервер → Клиент: 401 Unauthorized
   WWW-Authenticate: Basic realm="Tic-Tac-Toe"

3. Клиент → Сервер: Запрос с заголовком авторизации
   GET /api/game
   Authorization: Basic dXNlcjEyMzpzZWNyZXQxMjM=

   где dXNlcjEyMzpzZWNyZXQxMjM= = base64("user123:secret123")

4. Сервер: Проверяет credentials
   ✅ Успех → Возвращает данные (200 OK)
   ❌ Ошибка → Возвращает 401 Unauthorized
```

#### Формирование заголовка

**RFC 7617** определяет формат:

```
Authorization: Basic <credentials>

где <credentials> = base64(username + ":" + password)
```

**Пример:**

```java
String username = "user123";
String password = "secret123";
String credentials = username + ":" + password;
String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

// Заголовок
"Authorization: Basic " + encoded
// Authorization: Basic dXNlcjEyMzpzZWNyZXQxMjM=
```

#### ⚠️ Важные особенности

| Аспект                      | Описание                                                                |
| --------------------------- | ----------------------------------------------------------------------- |
| **Stateless**               | Каждый запрос содержит credentials (не используются сессии)             |
| **Автоматическая передача** | Браузер автоматически добавляет заголовок в последующие запросы         |
| **Безопасность**            | Обязательно использовать **HTTPS** (иначе передается открытым текстом!) |
| **Хранение**                | Пароли должны храниться в БД в хешированном виде                        |

---

### Изученные темы

- ✅ [HTTP Authentication (MDN)](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication)
- ✅ [RFC 7617 - Basic Authentication](https://datatracker.ietf.org/doc/html/rfc7617)
- ✅ [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- ✅ [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- ✅ [PostgreSQL Tutorial](https://www.postgresql.org/docs/)
- ✅ [Docker Compose](https://docs.docker.com/compose/)

---

## Chapter III: Практические задания

<div align="center">
<h3> Проект: Крестики-Нолики</h3>
<p>Использовал проект для серверной части с предыдущей недели <strong>Т03</strong></p>
</div>

---

## Задание 1. Добавление базы данных

<div>Интеграция PostgreSQL для хранения данных игр и пользователей
</div>

### Требования

#### 1️⃣ Настройка подключения к БД

В файле `application.properties` описал подключение к PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/s21_tic_tac_toe
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### 2️⃣ Рефакторинг хранилища данных

- ❌ **Удалил** класс-хранилище (потокобезопасную очередь)
- ✅ **Добавил** JPA аннотации к доменным моделям:
  - `@Entity` — для класса
  - `@Id` — для идентификатора
  - `@GeneratedValue` — для автогенерации ID

**Пример:**

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    // getters, setters
}
```

#### 3️⃣ Репозитории Spring Data JPA

Использовал `CrudRepository` в качестве родителя для репозиториев:

```java
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}

public interface GameRepository extends CrudRepository<Game, UUID> {
    List<Game> findByStatus(GameStatus status);
}
```

### ✅ Критерии выполнения

- [x] PostgreSQL настроена и подключена
- [x] Класс-хранилище удален
- [x] Модели аннотированы `@Entity`
- [x] Репозитории наследуются от `CrudRepository`
- [x] Приложение успешно запускается и работает с БД

---

## Задание 2. Добавление авторизации

Реализация Basic Authentication для защиты API

#### 1️⃣ Модель пользователя

Создал модель `User`:

```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
  private UUID id;
  private String login;
  private String passwordHash;
}

```

Реализовал поддержку на всех слоях:

- **Domain:** `User.java` (доменная модель)
- **Datasource:** `UserEntity.java` + `UserRepository.java`
- **Web:** `UserDTO.java`

#### 2️⃣ DTO для регистрации

```java
@Setter
@Getter
@AllArgsConstructor
public class UserDto {
  private UUID userId;
  private String login;
}
```

#### 3️⃣ Сервис авторизации

Создай `AuthService` с методами:

```java
public interface AuthService {

  /**
   * Регистрация пользователя
   *
   * @param request данные для регистрации (логин + пароль)
   * @return true если регистрация успешна, false если логин занят
   */
  boolean register(SignUpRequest request);

  /**
   * Авторизация по Basic Auth заголовку
   *
   * @param authorizationHeader заголовок вида "Basic base64(login:password)"
   * @return UUID пользователя, если авторизация успешна
   */
  Optional<UUID> authorize(String authorizationHeader);
}
```

**Реализация:**

```java
@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    @Override
    public boolean register(SignUpRequest request) {
        // 1. Проверить, что логин свободен
        // 2. Хешировать пароль (BCrypt)
        // 3. Сохранить пользователя
    }

    @Override
    public UUID authenticate(String authHeader) {
        // 1. Извлечь credentials из заголовка
        // 2. Декодировать base64
        // 3. Проверить логин и пароль
        // 4. Вернуть UUID пользователя
    }
}
```

#### 4️⃣ Контроллер авторизации

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

/**
   * Регистрация нового пользователя
   *
   * <p>POST /auth/register Body: { "login": "user123", "password": "password123" }
   *
   * <p>Response: 200 OK - { "success": true } 400 Bad Request - { "success": false }
   */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Boolean>> register(@RequestBody SignUpRequest request) {
        // Регистрация пользователя
        boolean success = authService.register(request);
        /.../
    }

    @PostMapping("/login")
    public ResponseEntity<UUID> login(
        @RequestHeader("Authorization") String authHeader) {
        // Авторизация и возврат UUID
        return authService
        .authorize(authorizationHeader)
        .map(userId -> ResponseEntity.ok(Map.of("userId", userId.toString())))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
```

#### 5️⃣ Фильтр авторизации

Создай `AuthFilter`, наследующийся от `GenericFilterBean`:

```java
@Component
public class AuthFilter extends GenericFilterBean {

    private final AuthService authService;

    @Override
    public void doFilter(ServletRequest request,
                        ServletResponse response,
                        FilterChain chain)
                        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        // 1. Валидация логина и пароля
        UUID userId = authService.authenticate(authHeader);

        if (userId != null) {
            // ✅ Успех - выполнить запрос
            chain.doFilter(request, response);
        } else {
            // ❌ Ошибка - вернуть 401
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
```

#### 6️⃣ Spring Security конфигурация

Создай `SecurityConfig`:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf().disable()  // Для REST API
            .authorizeHttpRequests(auth -> auth
                // Публичные endpoints
                .requestMatchers("/auth/signup", "/auth/login").permitAll()
                // Все остальные требуют авторизации
                .anyRequest().authenticated()
            )
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### ✅ Критерии выполнения

- [x] Модель `User` с UUID, логином, паролем
- [x] `SignUpRequest` DTO
- [x] `AuthService` с методами регистрации и аутентификации
- [x] `AuthController` с endpoints `/auth/signup` и `/auth/login`
- [x] `AuthFilter` для валидации запросов
- [x] `SecurityConfig` с настройкой доступа
- [x] Пароли хранятся в зашифрованном виде
- [x] `/auth/*` доступны без авторизации
- [x] Остальные endpoints требуют Basic Auth

---

## Задание 3. Добавил логику игры между двумя игроками

Реализация многопользовательского(онлайн и оффлайн) + поддежрка старого режима против бота игры

### 📋 Требования

#### 1️⃣ Состояния игры

Добавил enum `GameStatus`:

```java
@RequiredArgsConstructor
@Getter
public enum GameState {
  /** Ожидание подключения второго игрока */
  WAITING_FOR_PLAYERS(-101),

  /** Игра идёт, игроки делают ходы */
  PLAYING(-100),

  /** Ничья */
  DRAW(0),

  /** Один из игроков победил (UUID победителя хранится в Game.winnerId) */
  PLAYER_WIN(1);

  private final int value;
}
```

#### 2️⃣ Модель игры

Расширил модель `Game`:

```java
public class Game {
// ===== Мультиплеер поля =====
  private GameMode gameMode;

  /** UUID игрока 1 (создатель игры) */
  private UUID player1Id;

  /** UUID игрока 2 (второй игрок) */
  private UUID player2Id;

  /** Символ (X/O), которым играет игрок 1 */
  private PlayerType player1Symbol;

  /** Символ (X/O), которым играет игрок 2 */
  private PlayerType player2Symbol;

  /** UUID игрока, который должен сделать ход сейчас */
  private UUID currentTurnPlayerId;

  /** UUID победителя (если игра закончена победой) */
  private UUID winnerId;
}
```

#### 3️⃣ API Endpoints

Создал/улучшил следующие endpoints в `GameController`:

##### 🆕 Создание игры

```java
@PostMapping("/new")
  /**
   * Создать новую игру с выбором режима
   *
   * @param mode режим игры: "ai", "multiplayer", "local"
   * @param userId UUID авторизованного пользователя
   */
  @PostMapping("/new/{mode}")
  public ResponseEntity<GameDto> createGameWithMode(
      @PathVariable String mode, @AuthenticationPrincipal UUID userId) {

    GameMode gameMode;
    try {
      gameMode = GameMode.valueOf(mode.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid game mode: " + mode + ". Use: ai, multiplayer, or local");
    }

    Game newGame = gameService.createNewGame(gameMode, userId);
    return ResponseEntity.ok(DomainWebMapper.toWeb(newGame));
  }
```

##### 📋 Список доступных игр

```java
@GetMapping("/available")
public ResponseEntity<List<GameDTO>> getAvailableGames(
    @RequestHeader("Authorization") String authHeader) {

    // Вернуть игры со статусом WAITING_FOR_PLAYERS
    // Не показывать собственные игры
}
```

##### Присоединение к игре

```java
@PostMapping("/{gameId}/join")
public ResponseEntity<?> joinGame(
    @PathVariable UUID gameId,
    @RequestHeader("Authorization") String authHeader) {

    // Присоединить пользователя как player2
    // Изменить статус на PLAYER_TURN(player1Id)
    // Начать игру
}
```

##### Ход в игре

```java
@PutMapping("/{gameId}/move")
public ResponseEntity<GameDTO> makeMove(
    @PathVariable UUID gameId,
    @RequestHeader("Authorization") String authHeader,
    @RequestBody MoveRequest request) {

    // Проверить, что сейчас ход этого игрока
    // Если игра с компьютером - сделать ход компьютера после игрока
    // Проверить условие победы/ничьей
    // Обновить статус игры
    // Вернуть обновленное состояние
}
```

##### Получение игры

```java
@GetMapping("/{gameId}")
public ResponseEntity<GameDTO> getGame(
    @PathVariable UUID gameId,
    @RequestHeader("Authorization") String authHeader) {

    // Вернуть текущее состояние игры
}
```

#### 4️⃣ Алгоритм определения окончания игры

Улучшил алгоритм с использованием состояний:

```java
  private void updateGameState() {
    // Проверяем победителя
    PlayerType winnerSymbol = null;

    if (gameField.hasWinner(PlayerType.MIN)) {
      winnerSymbol = PlayerType.MIN;
    } else if (gameField.hasWinner(PlayerType.MAX)) {
      winnerSymbol = PlayerType.MAX;
    }

    // Если есть победитель
    if (winnerSymbol != null) {
      status = GameState.PLAYER_WIN;
      winnerId = getPlayerIdBySymbol(winnerSymbol);
    }
    // Если доска заполнена и нет победителя - ничья
    else if (gameField.isFull()) {
      status = GameState.DRAW;
      winnerId = null;
    }
    // Иначе игра продолжается
    else {
      status = GameState.PLAYING;
    }
  }
```

#### 5️⃣ Endpoint для информации о пользователе

```java
  @GetMapping("/me")
  public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal UUID userId) {
    return userService
        .getUserById(userId)
        .map(user -> ResponseEntity.ok(new UserDto(user.getId(), user.getLogin())))
        .orElse(ResponseEntity.notFound().build());
  }
```

### ✅ Критерии выполнения

- [x] Enum `GameStatus` со всеми состояниями
- [x] Модель `Game` содержит информацию о знаках игроков
- [x] Алгоритм использует состояния для определения окончания
- [x] `POST /game/new` - создание игры
- [x] `GET /game/available` - список доступных игр
- [x] `POST /game/{gameId}/join` - присоединение к игре
- [x] `PUT /game/{gameId}/move` - улучшенный с поддержкой PvP и PvC
- [x] `GET /game/{gameId}` - получение состояния игры
- [x] `GET /user/{userId}` - информация о пользователе
- [x] Корректная работа с двумя игроками
- [x] Корректная работа с компьютером

---

## Результат - рабочий продукт tic-tac-toe на onion архитектуре

После выполнения всех заданий:

<table>
<tr>
<td width="50%">

### Функциональность

- ✅ Регистрация и авторизация
- ✅ Защита API через Basic Auth
- ✅ Игра с компьютером
- ✅ Игра между двумя игроками
- ✅ Хранение данных в PostgreSQL
- ✅ RESTful API

</td>
<td width="50%">

### Технические навыки

- ✅ Spring Security
- ✅ Spring Data JPA
- ✅ PostgreSQL integration
- ✅ Docker Compose
- ✅ REST API design
- ✅ Authentication & Authorization

</td>
</tr>
</table>

---
