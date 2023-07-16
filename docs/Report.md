## Отчёт о проведённом тестировании

### Краткое описание
Тестируется приложение **aqa-shop** - веб-сервис для покупки туров. Предлагает покупку по карте или в кредит.<br/>
Приложение в собственной СУБД сохраняет информацию о том, успешно ли был совершён платёж и каким способом.<br/>
Приложение поддерживает работу с базами данных MySQL и PostrgeSQL.

В роли банковских сервисов выступает приложение **gate-simulator**. Оно симулирует работу сервиса платежей и кредитного сервиса.<br/>
### Количество тест-кейсов
Всего реализовано 85 тест-кейсов, из них 40 для покупки по карте, 40 для 1покупки в кредит и 5 для проверки БД.

### Процент успешных и не успешных тест-кейсов
* Завершились успешно 38 тест-кейсов (44.7%)
* Провалились 47 тест-кейсов (55.3%)

### Общие рекомендации
1. Доработать контроль вводимых данных для поля "Владелец"
2. Доработать сообщения предупреждений при некорректно заполненных или не заполненных полях
3. Реализовать проверку корректности значения поля "Владелец"
4. Проверить работу банковских сервисов (оплата по недействительной карте прошла успешно)