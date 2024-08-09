# Система управления задачами

Это простая система управления задачами, разработанная на Java с использованием Spring Boot. Она предоставляет RESTful API для создания, обновления, удаления и получения задач.

## Особенности

- Аутентификация и авторизация пользователей с использованием JWT
- CRUD операции для задач
- Фильтрация и пагинация при получении задач
- Добавление комментариев к задачам
- Swagger UI для документации API и тестирования

## Технологии

- Java 21
- Spring Boot 3.3.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- Gradle
- Docker и Docker Compose
- Swagger (OpenAPI 3)

## Предварительные требования

- JDK 17 или выше
- Gradle
- Docker и Docker Compose (для запуска приложения с PostgreSQL в контейнерах)

## Структура проекта

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── taskmanagerproject
│   │               ├── config
│   │               ├── controller
│   │               ├── exception
│   │               ├── model
│   │               ├── payload
│   │               ├── repository
│   │               ├── security
│   │               └── service
│   └── resources
│       └── application.properties
├── test
│   └── java
│       └── com
│           └── example
│               └── taskmanagerproject
│                   ├── controller
│                   └── service
build.gradle
docker-compose.yml
Dockerfile
README.md
```

## Локальная установка и запуск

1. Клонируйте репозиторий:
   ```
   git clone https://github.com/ваш-юзернейм/task-management-system.git
   cd task-management-system
   ```

2. Соберите приложение:
   ```
   ./gradlew build
   ```

3. Запустите приложение и базу данных с помощью Docker Compose:
   ```
   docker-compose up --build
   ```

   Это запустит приложение и базу данных PostgreSQL в Docker контейнерах.

4. Приложение будет доступно по адресу `http://localhost:8080`

5. Swagger UI будет доступен по адресу `http://localhost:8080/swagger-ui.html`

## Документация API

Подробная документация API доступна через Swagger UI. После запуска приложения перейдите по адресу `http://localhost:8080/swagger-ui.html` в вашем веб-браузере для изучения и тестирования API.

### Основные эндпоинты

- POST `/api/auth/register` - Регистрация нового пользователя
- POST `/api/auth/login` - Аутентификация пользователя и получение JWT токена
- GET `/api/tasks` - Получение списка задач (с возможностью фильтрации и пагинации)
- POST `/api/tasks` - Создание новой задачи
- PUT `/api/tasks/{id}` - Обновление существующей задачи
- DELETE `/api/tasks/{id}` - Удаление задачи
- POST `/api/tasks/{taskId}/comments` - Добавление комментария к задаче
- GET `/api/tasks/{taskId}/comments` - Получение комментариев к задаче

## Запуск тестов

Для запуска тестов используйте следующую команду:

```
./gradlew test
```

## Работа с базой данных

Проект использует PostgreSQL в качестве базы данных. При запуске через Docker Compose, база данных автоматически создается и настраивается согласно параметрам в `docker-compose.yml`.

Параметры подключения к базе данных:
- URL: jdbc:postgresql://localhost:5432/taskmanagerbd
- Пользователь: postgres
- Пароль: password

## Безопасность

Приложение использует JWT (JSON Web Tokens) для аутентификации и авторизации. После успешной аутентификации, клиент получает JWT токен, который необходимо включать в заголовок `Authorization` для всех последующих запросов.

## Известные проблемы и ограничения

- На данный момент нет функционала для обновления и удаления комментариев.
- Отсутствует функционал для управления пользователями (например, изменение пароля, удаление аккаунта).

## Планы по развитию

- Добавить функционал для управления пользователями
- Реализовать систему уведомлений о изменениях в задачах
- Добавить возможность прикрепления файлов к задачам
- Реализовать систему тегов для задач

## Контакты

- электронная почта: tema31000@gmail.com
- telegram: @tema310