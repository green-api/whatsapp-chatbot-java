# whatsapp-chatbot-java

whatsapp-chatbot-java - библиотека для интеграции с мессенджером WhatsApp через API
сервиса [green-api.com](https://green-api.com/). Чтобы воспользоваться библиотекой, нужно получить регистрационный токен
и ID аккаунта в [личном кабинете](https://console.green-api.com/). Есть бесплатный тариф аккаунта разработчика.

## API

Документация к REST API находится по [ссылке](https://green-api.com/docs/api/). Библиотека является обёрткой к REST API,
поэтому документация по ссылке выше применима и к самой библиотеке.

## Авторизация

Чтобы отправить сообщение или выполнить другие методы GREEN API, аккаунт WhatsApp в приложении телефона должен быть в
авторизованном состоянии. Для авторизации аккаунта перейдите в [личный кабинет](https://console.green-api.com/) и
сканируйте QR-код с использованием приложения WhatsApp.

## Установка

Maven

```
<dependency>
    <groupId>com.green-api</groupId>
    <artifactId>whatsapp-chatbot-java</artifactId>
    <version>{{version}}</version>
</dependency>
```

Gradle

```
implementation group: 'com.green-api', name: 'whatsapp-chatbot-java', version: 'version'
```

## Настройка

Перед запуском бота необходимо включить входящие уведомления в настройках экземпляра с помощью <a href="https://green-api.com/en/docs/api/account/SetSettings/">метода SetSettings</a>.

```json
"incomingWebhook": "yes",
"outgoingMessageWebhook": "yes",
"outgoingAPIMessageWebhook": "yes",
```

## Примеры

### Как инициализировать объект

После того как вы импортировали библиотеку в свой проект, вам необходимо сконфигурировать ваше приложение.
Для этого вам понадобится создать свой класс конфигурации и добавить в него следующие бины:

```java

@Configuration
public class BotDefaultConfigExample {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    public StateManager stateManager() {
        return new StateManagerHashMapImpl();
    }

    @Bean
    public BotFactory botFactory(RestTemplate restTemplate, StateManager stateManager) {
        return new BotFactory(restTemplate, stateManager);
    }
}
```

`RestTemplate` - стандартный класс Spring, который позволяет отправлять http запросы.
Вы можете сконфигурировать его сами или использовать дефолтную реализацию как в примере выше.

`StateManager` - класс библиотеки, который отвечает за управление состоянием бота.
По умолчанию сессионные данные хранятся в HashMap, но вы можете реализовать свою имплементацию интерфейса StateManager.
Если вы хотите добавить в состояние какие-то дефолтные значения, вы можете добавить их на стадии конфигурации
например так:

```java

@Configuration
public class BotDefaultConfigExample {
    @Bean
    public StateManager stateManager() {
        var stateData = new HashMap<String, Object>();
        stateData.put("defaultParameter", "value");

        return new StateManagerHashMapImpl(stateData);
    }
}
```

> ВАЖНО: В stateData зарезервирован параметр "scene", в нем хранится актуальная сцена для каждой сессии.
> Так как этот параметр используется библиотекой для переключения между сценами, не рекомендуется его переопределять на
> стадии конфигурации.

Далее необходимо прописать хост на который бот будет отправлять запросы в application.yml или application.property:

Если ваши инстансы начинаются с 7103:

```yaml
green-api:
  host: https://api.greenapi.com
  hostMedia: https://media.greenapi.com
```

Если нет:

```yaml
green-api:
  host: https://api.green-api.com
  hostMedia: https://media.green-api.com
```

`BotFactory` - класс библиотеки, который отвечает за конфигурирование объекта бота.
С помощью данного класса и метода `createBot()` вы можете инициализировать объект бота:

```java

@SpringBootApplication
public class BotStarterClassExample {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarterClassExample.class, args);
        var botFactory = context.getBean(BotFactory.class);

        var bot = botFactory.createBot(
            "{{instanceId}}",
            "{{token}}",
            new HandlerExample());

        bot.setStartScene(new FullStartScene());
    }
}
```

В методе `createBot()` четыре параметра:
`instanceId` и `token` нужно взять из параметров вашего инстанса в личном кабинете.
`handler` и `startScene` это объекты ваших классов в которых вы должны будете реализовать логику вашего бота.

`handler` - объект класса, который наследуется от абстрактного класса `BotHandler`. Вы можете переопределить в нем
методы, которые отвечают за обработку вебхуков о состоянии инстанса и девайса `StateInstanceChanged` и `DeviceInfo`.
Если вы не хотите обрабатывать эти типы уведомлений, вы можете просто не указывать handler в конструкторе, и использовать реализацию по умолчанию:

```java
public class BotStarterClassExample {
    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarterClassExample.class, args);
        var botFactory = context.getBean(BotFactory.class);

        var bot = botFactory.createBot(
            "{{instanceId}}",
            "{{token}}");

        bot.setStartScene(new FullStartScene());

        bot.startReceivingNotifications();
    }
}
```

`startScene` - это стартовая сцена с которой начинается общение с ботом. Она устанавливается сеттером `bot.setStartScene(new YourStartScene());`
Сцена - это объект класса, который наследуется от абстрактного класса `Scene`. Внутри сцен происходит обработка вебхуков и выполняется ваша бизнес-логика.
Ваш бот будет состоять из нескольких сцен, которые выполняются друг за другом в заданной вами последовательности.
Для каждого состояния одновременно может быть активна только одна сцена.
Примеры сцен будут продемонстрированы ниже.

### Как настроить инстанс

Чтобы начать получать входящие уведомления, нужно настроить инстанс. Открываем страницу личного кабинета
по [ссылке](https://console.green-api.com/). Выбираем инстанс из списка и кликаем на него. Нажимаем **Изменить**. В
категории **Уведомления** включаем все что необходимо получать.

### Как начать получать сообщения и отвечать на них

После создания своего наследника `BotHandler`, необходимо создать первую сцену.
Для этого создайте класс, который наследуется от `Scene` и переопределите метод-обработчик нужного типа.
Чаще всего вам будет нужен `processIncomingMessage()` который обрабатывает вебхуки о входящих сообщениях.
Придумайте для класса понятное название, стартовые сцены рекомендую помечать постфиксом `StartScene`.

Метод `processIncomingMessage()`, как и другие обработчики возвращает обновленное состояние бота.
В случае если состояние не поменялось, достаточно вернуть объект `currentState`.

Ссылка на пример: [BaseStartScene.java](../examples/base/BaseStartScene.java).

```java
public class BaseStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        answerWithText(incomingMessage, "Hello!", "message");

        return currentState;
    }
}
```

Чтобы запустить бота, нужно вызвать функцию `bot.startReceivingNotifications();`.
В этом примере бот имеет только одну сцену и ответит только на сообщение `message`.

```java
@SpringBootApplication
public class BotStarterClassExample {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarterClassExample.class, args);
        var botFactory = context.getBean(BotFactory.class);

        var bot = botFactory.createBot(
            "{{instanceId}}",
            "{{token}}");

        bot.setStartScene(new BaseStartScene());

        bot.startReceivingNotifications();
    }
}
```

### Как получать другие уведомления и обрабатывать тело уведомления

Получать можно не только входящие сообщения, но и исходящие, а так же их статусы и любый другие типы веб хуков.
Для этого просто переопределите нужный вам метод.

В этой сцене бот получает все входящие сообщения и выводит их в консоль. Остальные типы веб хуков игнорируются, их
обработчики
добавлены для наглядности.

Ссылка на пример: [EventStartScene.java](../examples/event/EventStartScene.java).

```java

@Log4j2
public class EventStartScene extends Scene {

    //  Для обработки входящих сообщений.
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info(incomingMessage); // Вывод сообщений

        return currentState;
    }

    //  Для обработки исходящих сообщений
    @Override
    public State processOutgoingMessage(MessageWebhook outgoingMessage, State currentState) {
        return super.processOutgoingMessage(outgoingMessage, currentState);
    }

    //  Для обработки статусов исходящих сообщений
    @Override
    public State processOutgoingMessageStatus(OutgoingMessageStatus outgoingMessageStatus, State currentState) {
        return super.processOutgoingMessageStatus(outgoingMessageStatus, currentState);
    }

    //  Для обработки входящих звонков
    @Override
    public State processIncomingCall(IncomingCall incomingCall, State currentState) {
        return super.processIncomingCall(incomingCall, currentState);
    }

    //  Для обработки блокировок чата
    @Override
    public State processIncomingBlock(IncomingBlock incomingBlock, State currentState) {
        return super.processIncomingBlock(incomingBlock, currentState);
    }
}
```

### Получение уведомлений через HTTP API

Получать входящие уведомления (сообщения, статусы) можно через HTTP API запросы по аналогии, как реализованы остальные методы Green API. При этом гарантируется хронологический порядок следования уведомлений в той последовательности, в которой они были получены FIFO. Все входящие уведомления сохраняются в очереди и ожидают своего получения в течение 24 часов.

Для получения входящих уведомлений требуется выполнить последовательно вызов двух методов <a href="https://green-api.com/docs/api/receiving/technology-http-api/ReceiveNotification/">ReceiveNotification</a> и <a href="https://green-api.com/docs/api/receiving/technology-http-api/DeleteNotification/">DeleteNotification</a>. Метод ReceiveNotification выполняет получение входящего уведомления. Метод DeleteNotification подтверждает успешное получение и обработку уведомления. Подробнее о методах смотрите в соответствующих разделах ReceiveNotification и DeleteNotification.

### Как фильтровать входящие сообщения

Фильтрация по типу вебхука происходит автоматически с помощью переопределения нужных методов, как описано в пункте выше,
но как фильтровать сообщения?

Так как каждое уведомление автоматически кастится до java объекта, вы можете фильтровать сообщения по любому полю
объекта самостоятельно.
С описанием структуры объектов уведомлений можно ознакомиться по этой
ссылке: [Документация](https://green-api.com/docs/api/receiving/notifications-format/type-webhook/)
Для удобства все java объекты и поля названы аналогично документации:

| Java объект                          | Webhook's json объект                                                                                                                                     |
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `TextMessageWebhook`                 | [TextMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/TextMessage/)                                                |
| `TemplateMessageWebhook`             | [TemplateMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/TemplateMessage/)                                        |
| `StickerMessageWebhook`              | [StickerMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/StickerMessage/)                                          |
| `ReactionMessageWebhook`             | [ReactionMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ReactionMessage/)                                        |
| `QuotedMessageWebhook`               | [QuotedMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/QuotedMessage/)                                            |
| `PollUpdateMessageWebhook`           | [PollUpdateMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/PollUpdateMessage/)                                    |
| `PollMessageWebhook`                 | [PollMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/PollMessage/)                                                |
| `LocationMessageWebhook`             | [LocationMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/LocationMessage/)                                        |
| `ListMessageWebhook`                 | [ListMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ListMessage/)                                                |
| `GroupInviteMessageWebhook`          | [GroupInviteMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/GroupInviteMessage/)                                  |
| `FileMessageWebhook`                 | [imageMessage, videoMessage, documentMessage, audioMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ImageMessage/) |
| `ExtendedTextMessageWebhook`         | [ExtendedTextMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ExtendedTextMessage/)                                |
| `ButtonsMessageWebhook`              | [ButtonsMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ButtonsMessage/)                                          |
| `ContactMessageWebhook`              | [ContactMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ContactMessage/)                                          |
| `ContactsArrayMessageWebhook`        | [ContactMessage](https://green-api.com/docs/api/receiving/notifications-format/incoming-message/ContactsArrayMessage/)                                    |
| `TemplateButtonsReplyMessageWebhook` | [TemplateButtonsReplyMessage](https://green-api.com/docs/api/receiving/notifications-format/selected-buttons/TemplateButtonsReplyMessage/)                |
| `ButtonsResponseMessageWebhook`      | [ButtonsResponseMessage](https://green-api.com/docs/api/receiving/notifications-format/selected-buttons/ButtonsResponseMessage/)                          |
| `ListResponseMessageWebhook`         | [ListResponseMessage](https://green-api.com/docs/api/receiving/notifications-format/selected-buttons/ListResponseMessage/)                                |

Вы можете самостоятельно проверить и преобразовать полученное уведомление в нужный вам тип.

Ссылка на пример: [MediaStartScene.java](../examples/media/MediaStartScene.java).

```java
public class MediaStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        if (incomingMessage instanceof ContactMessageWebhook) {
            answerWithText(incomingMessage, "This is a contact message");

        } else if (incomingMessage instanceof LocationMessageWebhook) {
            answerWithText(incomingMessage, "This is location message");

        } else if (incomingMessage instanceof FileMessageWebhook) {
            answerWithText(incomingMessage, "This is a message with a file");
        }

        return currentState;
    }
}
```

Так же для удобства и улучшения читаемости кода, методы класса `Scene` перегружены, в них встроены наиболее
востребованные фильтры по тексту входящего сообщения.
Примеры использования методов данного класса будут описаны ниже. Если же вы хотите сделать условие не выполняя методов
класса `Scene`,
вы можете воспользоваться методами класса `Filter` данной библиотеки, которые возвращают `boolean` значение:

| Название фильтра                                                                      | Описание                                                                                       |
|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| `Filter.isSenderIdExpected(MessageWebhook messageWebhook, String expectedSenderId)`   | Возвращает `true`, если `expectedSenderId` равен идентификатору отправителя в `messageWebhook` |
| `Filter.isMessageTextRegex(MessageWebhook messageWebhook, Pattern regexPattern)`      | Возвращает `true`, если `regexPattern` совпадает с текстом в `messageWebhook`                  |
| `Filter.isMessageTextExpected(MessageWebhook messageWebhook, String expectedMessage)` | Возвращает `true`, если `expectedMessage` равен тексту в `messageWebhook`                      |

#### Пример

В этом примере бот отправит сообщение и файл в ответ на команду `rates`.
Отправка сообщения методом `answerWithText()` запускается только в ответ на команду `rates` благодаря тому, что третьим
параметром в метод `answerWithText`
передана строка `"rates"`. Данный механизм реализован во всех методах класса `Scene`. Если вы хотите отвечать на все
сообщения, без фильтра, просто не указывайте третий параметр.
Так-же вы можете вместо строки передать в качестве третьего параметра regex паттерн.

Отправка файла методом `answerWithUploadFile()` запускается только в ответ на команду `rates` благодаря тому, что метод
находится в блоке `if` в условии которого выполняется метод
`Filter.isMessageTextExpected(incomingMessage, "rates")`.

Ссылка на пример: [FiltersStartScene.java](../examples/filters/FiltersStartScene.java).

```java
public class FiltersStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

        answerWithText(incomingMessage, "You see this because you wrote \"rates\"", "rates"); //фильтрация перегруженным методом

        if (Filter.isMessageTextExpected(incomingMessage, "rates")) {                         //фильтрация методом класса Filter
            answerWithUploadFile(incomingMessage, new File("src/main/resources/data/rates.png"));
        }

        return currentState;
    }
}
```

### Как управлять сценами

Для управления сценами в классе `Scene` есть специальные методы. Их необходимо выполнять после ключевого слова `return`
внутри активной сцены.
Данные методы меняют параметр `scene` в состоянии чата, следующее сообщение из выбранного чата попадет в новую сцену.

| Методы класса `Scene`                                    | Описание                                                  |
|----------------------------------------------------------|-----------------------------------------------------------|
| `activateNextScene(State currentState, Scene nextScene)` | Активирует следующую сцену `nextScene` для текущего чата. |
| `activateStartScene(State currentState)`                 | Активирует стартовую сцену для текущего пользователя.     |

Полный перечень методов доступных на сцене, описан в конце файла.

### Как управлять состоянием пользователя

Чтобы управлять состоянием пользователя, достаточно внести изменения в объект `currentState` внутри сцены и вернуть его
с помощью `return`.
В конце каждой сцены происходит автоматическое обновление состояния.

Для управления состоянием напрямую нужно использовать объект `stateManager` который является инстансом вашей
имплементацией интерфейса `StateManager`.
Данный объект доступен в любой сцене, так как является одним из ее полей.
В менеджере есть методы которые совершают основные crud операции над состоянием. Также у вас есть возможность сохранить
данные чата в его состоянии.

| Метод менеджера     | Описание                                                                                  |
|---------------------|-------------------------------------------------------------------------------------------|
| `get()`             | Возвращает состояние выбранного чата.                                                     |
| `create()`          | Создает новое состояние для чата.                                                         |
| `update()`          | Обновляет состояние (в дефолтной реализации интерфейса метод не представлен)              |
| `delete()`          | Удаляет состояние пользователя.                                                           |
| `getStateData()`    | Возвращает данные состояния                                                               |
| `setStateData()`    | Если состояние существует, перезаписывает данные состояния                                |
| `updateStateData()` | Если состояние существует, обновляет данные состояния (put)                               |
| `deleteStateData()` | Если состояние существует, то очищает данные состояния (устанавливает дефолтные значения) |

> Идентификатором состояния является ID чата (поле chatId, не путать с senderId).

В качестве примера был создан бот для регистрации пользователя.

Ссылка на пример: [state](../examples/state).

```java
public class StateStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        answerWithText(incomingMessage, "Hello. Tell me your username.");

        return activateNextScene(currentState, new InputUsernameScene());
    }
}
```

```java
public class InputUsernameScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var stateData = currentState.getData();

        var username = getText(incomingMessage);
        if (username != null && username.length() <= 20 && username.length() >= 5) {
            stateData.put("username", username);
            currentState.setData(stateData);

            answerWithText(incomingMessage, "Please, send password");

            activateNextScene(currentState, new InputPasswordScene());

        } else {
            answerWithText(incomingMessage, "invalid username");
        }

        return currentState;
    }
}
```

```java
public class InputPasswordScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var stateData = currentState.getData();

        var password = getText(incomingMessage);
        if (password != null && password.length() <= 20 && password.length() >= 8) {
            stateData.put("password", password);
            currentState.setData(stateData);

            answerWithText(incomingMessage, String.format("""
                Successful account creation.
                Your username: %s.
                Your password: %s.
                """, stateData.get("username"), password));

            return activateStartScene(currentState);

        } else {
            answerWithText(incomingMessage, "invalid password");
        }

        return currentState;
    }
}
```

### Пример бота

В качестве примера был создан бот демонстрирующий отправку методов класса Scene.

Запуск бота происходит командой - /start
После запуска необходимо выбрать метод из меню, и бот выполнит его.

Ссылка на пример: [full](../examples/full).

Стартовая сцена ждет команду `/start`, после чего отправляет меню и активирует следующую сцену `ChooseScene`.

```java
public class FullStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

        var greetingText =
            """
                Please, choose Scene's method and I execute it. 
                            
                1. answerWithText();
                2. answerWithUrlFile();
                3. answerWithPoll();
                4. answerWithLocation();
                5. answerWithContact();
                6. Exit.
                """;

        var resp = answerWithText(incomingMessage, greetingText, "/start");
        if (resp == null) {
            var sendMessageResp = answerWithText(incomingMessage, "Hi, this is test bot.\nPlease, send me a command - /start");

            return currentState;
        }

        return activateNextScene(currentState, new ChooseScene());
    }
}
```

Вторая сцена ждет ответа пользователя и запускает выполнение нужного метода.
В случае выбора `answerWithUrlFile()`, запускается сцена `InputLinkScene`

```java
public class ChooseScene extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var text = getText(incomingMessage);
        if (text == null) {
            answerWithText(incomingMessage, "PLease send a text message!");
            return currentState;
        }

        switch (text) {
            case "1" -> {
                answerWithText(incomingMessage, "Hi! This is answerWithText!");
                return currentState;
            }
            case "2" -> {
                answerWithText(incomingMessage, "Send me the link on File:");
                return activateNextScene(currentState, new InputLinkScene());
            }
            case "3" -> {
                var options = new ArrayList<Option>();
                options.add(new Option("Red"));
                options.add(new Option("Blue"));
                options.add(new Option("Green"));
                options.add(new Option("Pink"));
                answerWithPoll(incomingMessage, "choose color", options, false);
                return currentState;
            }
            case "4" -> {
                answerWithLocation(incomingMessage, "Home", "Cdad. de La Paz 2969, Buenos Aires", -34.5553558, -58.4642510);
                return currentState;
            }
            case "5" -> {
                var contact = Contact.builder()
                    .firstName("first")
                    .lastName("last")
                    .middleName("middle")
                    .company("Green API")
                    .phoneContact(11111111111L)
                    .build();
                answerWithContact(incomingMessage, contact);
                return currentState;
            }
            case "6" -> {
                answerWithText(incomingMessage, "Goodbye!");
                return activateStartScene(currentState);
            }
            default -> {
                answerWithText(incomingMessage, "Please send numbers - 1, 2, 3, 4, 5 or 6");
                return currentState;
            }
        }
    }
}
```

Данная сцена ждет чтобы пользователь отправил ссылку на файл.
Если ссылка корректная, отправляет файл и возвращает на сцену выбора `ChooseScene`.

```java
public class InputLinkScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var fileUrl = getText(incomingMessage);

        if (fileUrl != null) {
            try {
                answerWithUrlFile(incomingMessage, "This is your file!", fileUrl, "testFile");
            } catch (Exception e) {
                answerWithText(incomingMessage, "invalid link! Please send me a link, for example https://greenapi.com");
            }
        } else {
            answerWithText(incomingMessage, "Please send me a link!");

            return currentState;
        }

        return activateNextScene(currentState, new ChooseScene());
    }
}
```

### Список методов класса Scene

| Методы класса `Scene`                                                                                                       | Описание                                                                                     |
|-----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| `activateNextScene(State currentState, Scene nextScene)`                                                                    | Активирует следующую сцену `nextScene` для текущего чата.                                    |
| `activateStartScene(State currentState)`                                                                                    | Активирует стартовую сцену для текущего пользователя.                                        |
| `getText(MessageWebhook messageWebhook)`                                                                                    | Возвращает текст сообщения, если оно текстовое, если нет возвращает `null`                   |
| `answerWithText(MessageWebhook messageWebhook, String text)`                                                                | Отвечает текстом на входящее сообщение.                                                      |
| `answerWithUploadFile(MessageWebhook messageWebhook, String caption, File file)`                                            | Загружает и отправляет файл в ответ на входящее сообщение. `Сaption` - не обязательное поле. |
| `answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String fileName)`                             | Отправляет файл из url в ответ на входящее сообщение. `Сaption` - не обязательное поле.      |
| `answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude)` | Отправляет геолокация в ответ на входящее сообщение.                                         |
| `answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers)`              | Отправляет опрос в ответ на входящее сообщение.                                              |
| `answerWithContact(MessageWebhook messageWebhook, Contact contact)`                                                         | Отправляет контакт в ответ на входящее сообщение.                                            |

> В перегруженном варианте методы ответов на сообщения могут содержать дополнительные параметры `expectedMessage`
> и `regexPattern`.
> Если текст входящего сообщения совпадает с условием, метод выполнится и вернет ответ метода согласно документации,
> если нет, то метод вернет `null`.

## Документация по методам сервиса

[Документация по методам сервиса](https://green-api.com/docs/api/)

## Лицензия

Лицензировано на условиях [
Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
](https://creativecommons.org/licenses/by-nd/4.0/).
[LICENSE](../../../../../../../LICENSE).
