# Задание 2.1. Тест API

В рамках решения задания былы созданы тест-кейсы и написаны автотесты на Java и JUnit5.

## Что в проекте
- [TEST_CASES.MD](./TEST_CASES.MD) — полный тест-план.
- [BUG_REPORTS.md](./BUG_REPORTS.md) — баг-репорты по результатам прогона.
- `pom.xml` — зависимости и настройки Maven/JUnit 5.

## Инструкция по запуску
1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/dellup/avito-qa.git
   ```
   Или скачайте ZIP-архив репозитория и распакуйте его.

2. Перейдите в папку с заданием:
   ```bash
   cd avito-qa/Task2
   ```

3. Проверьте, что установлен Java 21:
   ```bash
   java -version
   ```
   В выводе должна быть версия `21`.

4. Если Java не установлена:
   - Установите JDK 21 (рекомендую Temurin 21): https://adoptium.net/temurin/releases/?version=21
   - На Windows можно установить через `winget`:
     ```powershell
     winget install EclipseAdoptium.Temurin.21.JDK
     ```
   - После установки закройте и откройте терминал заново.
   - Повторно проверьте:
     ```bash
     java -version
     ```

5. Проверьте, что установлен Maven:
   ```bash
   mvn -v
   ```

6. Если Maven не установлен:
   - Установите Maven с официального сайта: https://maven.apache.org/download.cgi
   - На Windows можно установить через `winget`:
     ```powershell
     winget install Apache.Maven
     ```
   - Убедитесь, что `mvn` доступен в `PATH`.
   - Повторно проверьте:
     ```bash
     mvn -v
     ```

7. Запустите тесты:
   ```bash
   mvn clean test
   ```
   Успешный запуск: в конце есть `BUILD SUCCESS`.

## Почему может быть BUILD FAILURE
`BUILD FAILURE` в этом проекте чаще всего означает не ошибку сборки, а падение части тестов по асертам (из-за багов сервиса).

Проверка, что код собирается:
```bash
mvn clean test-compile
```

Запуск с отчетом, но без падения процесса в PowerShell:
```powershell
mvn test "-Dmaven.test.failure.ignore=true"
```

Детальные результаты прогона:
`Task2/target/surefire-reports`
