## Тестирование приложения "aqa-shop"
1. [План тестирования](docs/Plan.md)
2. [Отчёт о проведённом тестировании](docs/Report.md)
3. [Отчёт о проведённой автоматизации](docs/Summary.md)

## Порядок запуска автотестов
Перед запуском тестов необходимо установить следующее ПО:
- Java 11;
- IntelliJ Idea
- Docker

Команды выполняются в терминале в корневой директории проекта.
### Подготовка окружения
1. Запустить приложение Docker.
2. Запустить базы данных и gate-simulator командой `docker-compose up -d`. 
Первый запуск может занять продолжительное время. Необходимо подождать, когда статус контейнеров postgres, mysql и node-app станет "Started"
3. Запустить SUT:
    - для проверки БД mysql: `java -jar .\artifacts\aqa-shop.jar`
    - для проверки БД postgres: `java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar .\artifacts\aqa-shop.jar`

### Запуск тестов
В новом окне терминала выполнить команду:
- для проверки БД mysql: `.\gradlew clean test "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app"`
- для проверки БД postgres: `.\gradlew clean test "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app"`

### Получение отчёта о тестировании
1. Выполнить команду: `.\gradlew allureServe`. Отчёт откроется в браузере по умолчанию.

### Завершение работы
1. Завершить работу SUT. Для этого в терминале нажать Ctrl+C
2. Удалить базы данных и gate-simulator командой `docker-compose down`