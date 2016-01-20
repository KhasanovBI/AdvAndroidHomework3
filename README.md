# Advanced Android Homework 3
Курс. Углубленная мобильная разработка на Android. Домашнее задание №3.

Задание: На основании приложения из [домашнего задания №2] [1] получить полноценное приложение для общения. Пользоваться можно любыми библиотеками.

#### Краткое описание приложения

Пишем приложение - простой клиент к чат серверу. Хост 188.166.49.215 Порт 7788
Из пройденного используется все и если возможно даже больше:

- Клиент стартует
- Коннектится к серверу (необходимо обработать ситуацию офлайна и недоступности сервера)
- Авторизуется на сервере (необходимо обработать различные ошибки)
- Запрашивает список контактов получает ответ и обрабатывает его
- По выбору пользователя заходит в чат с другим пользователем
- Может читать сообщения
- Может отправлять сообщения
- Может показывать информацию о пользователях
- Может импортировать свои контакты на сервер чтобы найти пользователей
- Может отправлять не только текст

####Что изменилось по сравнению с ДЗ№2

Необходимый дополнительный функционал:

- Контакт лист
- Получение контактов с сервера
- Создание новых контактов
- Удаление контактов
- Импорт контактов из устройства
- Отправка сообщений контакту
- Отправка и прием аватарок
- Каналы как класс исчезли

Протокол работы с сервером
Запросы от клиента на сервер
1. Регистрация

{
 "action":"register",
 "data":{
  "login":"MY_LOGIN",
  "pass":"MD5_FROM_PASS",
  "nick":"NICKNAME"
 }
}

2. Авторизация

{
 "action":"auth",
 "data":{
  "login":"MY_LOGIN",
  "pass":"MD5_FROM_PASS"
 }
}

3. Запросить информацию о пользователе

{
 "action":"userinfo",
 "data": {
  "user":"USER_ID",
  "cid":"MY_USER_ID",
  "sid":"MY_SESSION_ID"
 }
}

4. Запрос контакт листа

{
 "action":"contactlist", 
 "data": {
        "cid":"MY_USER_ID",
        "sid":"MY_SESSION_ID"
    }
}

5. Добавление в контакт лист

{
    "action":"addcontact", 
    "data": {
        "uid":"USER_ID",
        "cid":"MY_USER_ID",
        "sid":"MY_SESSION_ID"
    }
}

6. Удаление из контакт листа

{
    "action":"delcontact", 
    "data": {
        "uid":"USER_ID",
        "cid":"MY_USER_ID",
        "sid":"MY_SESSION_ID"
    }
}

7. Отправка сообщения

{
    "action":"message",
    "data": {
        "cid":"MY_USER_ID",
        "sid":"MY_SESSION_ID",
        "uid":"USER_ID",
        "body":"MESSAGE",
        "attach": {
            "mime":"MIME_TYPE_OF_ATTACH",
            "data":"BASE64_OF_ATTACH"
        }
    }
}

8. Импорт контактов
 
{
    "action":"import",
    "data":{
        "contacts":[
            {
                "myid":"MY_ID",
                "name":"NAME",
                "phone":"PNONE",
                "email":"EMAIL"
            },
            {
                "myid":"MY_ID",
                "name":"NAME",
                "phone":"PNONE",
                "email":"EMAIL" 
            }
        ]
    }
}

9. Изменить свою информацию
 
{
    "action":"setuserinfo",
    "data": {
        "user_status":"STATUS_STRING",
        "cid":"MY_USER_ID",
        "sid":"MY_SESSION_ID"
        "email":"EMAIL",
        "phone":"PHONE",
        "picture":"BASE64_SMALL_PIC"
    }
 }


Ответы сервера на клиент
1. Welcome сообщение приходит при конекте к серверу

{
 "action":"welcome",
 "message": "WELCOME_TEXT",
 "time":UNIXTIMESTAMP
}

2. Ответ на авторизацию

{
 "action":"auth",
 "data":{
  "status":[0-9]+,
  "error":"TEXT_OF_ERROR",
  "sid":"SESSION_ID",
  "uid":"USER_ID"
 }
}

3. Ответ на регистрацию

{
 "action":"register",
 "data":{
  "status":[0-9]+,
  "error":"TEXT_OF_ERROR"
 }
}

4. Ответ на запрос информации о пользователе

{
 "action":"userinfo",
 "data":{
  "status":[0-9]+,
  "error":"TEXT_OF_ERROR",
  "nick":"NICKNAME",
        "email":"EMAIL",
        "phone":"PHONE",
        "picture":"BASE64_SMALL_PIC"
  "user_status":"STATUS_STRING"
 }
}

5. Ответ на запрос контакт листа

{
    "action":"contactlist", 
    "data":{
        "status":"[0-9]+",
        "error":"TEXT_OF_ERROR",
        "list":[
            {
                "myid":"YOUR_ID",
                "uid":"UID",
                "nick":"NICK NAME",
                "email":"EMAIL",
                "phone":"PHONE",
                "picture":"BASE64_SMALL_PIC"
            },
            {
                "myid":"YOUR_ID",
                "uid":"UID",
                "nick":"NICK NAME",
                "email":"EMAIL",
                "phone":"PHONE",
                "picture":"BASE64_SMALL_PIC"
            },
        ]
    }
}

6. Добавление контакта

{
    "action":"addcontact", 
    "data": {
        "status":"[0-9]+",
        "error":"TEXT_OF_ERROR",
        "user":{
                "uid":"UID",
                "nick":"NICK NAME",
                "email":"EMAIL",
                "phone":"PHONE"
        }
    }
}

7. Удаление контакта

{
    "action":"delcontact", 
    "data": {
        "status":"[0-9]+",
        "error":"TEXT_OF_ERROR",
        "uid":"UID"
    }
}

8. Импорт контактов

{
    "action":"import", 
    "data":{
        "status":"[0-9]+",
        "error":"TEXT_OF_ERROR",
        "list":[
            {
                "myid":"YOUR_ID",
                "uid":"UID",
                "nick":"NICK NAME",
                "email":"EMAIL",
                "phone":"PHONE"
            },
            {
                "myid":"YOUR_ID",
                "uid":"UID",
                "nick":"NICK NAME",
                "email":"EMAIL",
                "phone":"PHONE"
            },
        ]
    }
}

9. Изменить свою информацию

{
    "action":"setuserinfo",
    "data":{
        "status":[0-9]+,
        "error":"TEXT_OF_ERROR"
    }
} 

10. Отправка сообщения

{
    "action":"message", 
    "data":{
        "status":"[0-9]+",
        "error":"TEXT_OF_ERROR"
    }
}


События присылаемые с сервера на клиент
1. Новое сообщение

{
    "action":"ev_message",
    "data":{
        "from":"USER_ID",
        "nick":"NICKNAME",
        "body":"TEXT_OF_MESSAGE",
        "time":"TIMESPAMT",
        "attach": {
            "mime":"MIME_TYPE_OF_ATTACH",
            "data":"BASE64_OF_ATTACH"
        }
    }
 }

Коды ошибок

// Error codes
const (
 ErrOK              = 0 // All OK
 ErrAlreadyExist    = 1 // Login or Nickname or Channel already exist
 ErrInvalidPass     = 2 // Invalid login or password
 ErrInvalidData     = 3 // Invalid JSON
 ErrEmptyField      = 4 // Empty Nick, Login, Password or Channel
 ErrAlreadyRegister = 5 // User is already registered
 ErrNeedAuth        = 6 // User has to auth
 ErrNeedRegister    = 7 // User has to register
 ErrUserNotFound    = 8 // User not found by uid
)


[1]: https://github.com/KhasanovBI/AdvAndroidHomework2
[2]: http://ninjamock.com/s/L856T