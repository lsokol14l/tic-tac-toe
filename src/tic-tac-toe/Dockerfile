# Build stage
FROM gradle:8.14.0-alpine AS build
WORKDIR /build

# Копируем только файлы конфигурации для кеширования зависимостей
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Скачиваем зависимости (без сборки)
RUN gradle dependencies --no-daemon

# Копируем исходники
COPY src ./src

# Собираем jar (без тестов)
RUN gradle bootJar -x test --no-daemon

# Run stage
FROM eclipse-temurin:18-jre-alpine
WORKDIR /app

# Копируем war-файл из build stage
COPY --from=build /build/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]