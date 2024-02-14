# whatsapp-chatbot-java

| Support links                                                                                                                           | Guides & News                                                                                                                                  |
|-----------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| [![Support](https://img.shields.io/badge/support@green--api.com-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:support@greenapi.com) [![Support](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/greenapi_support_eng_bot) [![Support](https://img.shields.io/badge/WhatsApp-25D366?style=for-the-badge&logo=whatsapp&logoColor=white)](https://wa.me/77273122366) | [![Guides](https://img.shields.io/badge/YouTube-%23FF0000.svg?style=for-the-badge&logo=YouTube&logoColor=white)](https://www.youtube.com/@greenapi-en) [![News](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/green_api) [![News](https://img.shields.io/badge/WhatsApp-25D366?style=for-the-badge&logo=whatsapp&logoColor=white)](https://whatsapp.com/channel/0029VaLj6J4LNSa2B5Jx6s3h) |

[Ссылка русскоязычную инструкцию](src/main/java/com/greenapi/chatbot/docs/README_RU.md)

whatsapp-chatbot-java - library for integration with WhatsApp messenger via API
service [green-api.com](https://greenapi.com/). To use the library, you need to obtain a registration token
and account ID in [personal account](https://console.greenapi.com/). There is a free developer account plan.

## API

Documentation for the REST API can be found at [link](https://greenapi.com/en/docs/api/). The library is a wrapper for the REST API,
therefore the documentation in the link above also applies to the library itself.

## Authorization

To send a message or perform other GREEN API methods, the WhatsApp account in the phone app must be in
authorized state. To authorize your account, go to [personal account](https://console.green-api.com/) and
scan the QR code using the WhatsApp application.

## Installation

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

## Setup

Before launching the bot you should enable incoming notifications in instance settings by using <a href="https://green-api.com/en/docs/api/account/SetSettings/">SetSettings method</a>.

```
"incomingWebhook": "yes",
"outgoingMessageWebhook": "yes",
"outgoingAPIMessageWebhook": "yes",
```

## Examples

### How to initialize an object

Once you have imported the library into your project, you need to configure your application.
To do this, you will need to create your own configuration class and add the following beans to it:

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

`RestTemplate` is a standard Spring class that allows you to send http requests.
You can configure it yourself or use the default implementation as in the example above.

`StateManager` is a library class that is responsible for managing the bot's state.
By default, session data is stored in a HashMap, but you can implement your own implementation of the StateManager interface.
If you want to add some default values to the state, you can add them at the configuration stage
for example like this:

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

> IMPORTANT: The "scene" parameter is reserved in stateData; it stores the current scene for each session.
> Since this parameter is used by the library to switch between scenes, it is not recommended to override it at the configuration stage.

Next, you need to register the host to which the bot will send requests in application.yml or application.property:

If your instances start with 7103:
```yaml
green-api:
   host: https://api.greenapi.com
   hostMedia: https://media.greenapi.com
```
If not:
```yaml
green-api:
   host: https://api.green-api.com
   hostMedia: https://media.green-api.com
```

`BotFactory` is a library class that is responsible for configuring the bot object.
Using this class and the `createBot()` method you can initialize a bot object:

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

The `createBot()` method has four parameters:
`instanceId` and `token` must be taken from the parameters of your instance in your personal account.
`handler` and `startScene` are objects of your classes in which you will need to implement the logic of your bot.

`handler` is a class object that inherits from the abstract class `BotHandler`. You can override in it
methods that are responsible for processing webhooks about the state of the instance and device `StateInstanceChanged` and `DeviceInfo`.
If you don't want to handle these types of notifications, you can simply omit the handler in the constructor, and use the default implementation:

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

`startScene` is the starting scene from which communication with the bot begins. It is set by the setter `bot.setStartScene(new YourStartScene());`
A scene is a class object that inherits from the abstract class `Scene`. Inside the scenes, webhooks are processed and your business logic is executed.
Your bot will consist of several scenes that are executed one after another in the sequence you specify.
Only one scene can be active at a time per state.
Sample scenes will be shown below.

### How to set up an instance

To start receiving incoming notifications, you need to configure your instance. Open your personal account page
via [link](https://console.greenapi.com/). Select an instance from the list and click on it. Click **Change**. IN
The **Notifications** categories include everything you need to receive.

### How to start receiving and responding to messages

After creating your `BotHandler` successor, you need to create the first scene.
To do this, create a class that inherits from `Scene` and override the handler method of the desired type.
Most often you will need `processIncomingMessage()` which handles webhooks about incoming messages.
Come up with a clear name for the class; I recommend marking the starting scenes with the postfix `StartScene`.

The `processIncomingMessage()` method, like other handlers, returns the updated state of the bot.
If the state has not changed, it is enough to return the `currentState` object.

Link to example: [BaseStartScene.java](src/main/java/com/greenapi/chatbot/examples/base/BaseStartScene.java).

```java
public class BaseStartScene extends Scene {
     @Override
     public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
         answerWithText(incomingMessage, "Hello!", "message");

         return currentState;
     }
}
```

To start the bot, you need to call the `bot.startReceivingNotifications();` function.
In this example, the bot only has one scene and will only respond to the `message`.

```java
@SpringBootApplication
public class BotStarterClassExample {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarterClassExample.class, args);
        var botFactory = context.getBean(BotFactory.class);

        var bot = botFactory.createBot(
            "{{instanceId}}",
            "{{token}}",
            new HandlerExample(),
            new BaseStartScene());

        bot.startReceivingNotifications();
    }
}
```

### How to receive other notifications and handle the notification body

You can receive not only incoming messages, but also outgoing ones, as well as their statuses and any other types of web hooks.
To do this, simply override the method you need.

In this scene, the bot receives all incoming messages and outputs them to the console. Other types of web hooks are ignored, their handlers
added for clarity.

Link to example: [EventStartScene.java](src/main/java/com/greenapi/chatbot/examples/event/EventStartScene.java).

```java
@Log4j2
public class EventStartScene extends Scene {

     // To process incoming messages.
     @Override
     public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
         log.info(incomingMessage); // Output messages

         return currentState;
     }
    
     // To process outgoing messages
     @Override
     public State processOutgoingMessage(MessageWebhook outgoingMessage, State currentState) {
         return super.processOutgoingMessage(outgoingMessage, currentState);
     }
    
     // To process the statuses of outgoing messages
     @Override
     public State processOutgoingMessageStatus(OutgoingMessageStatus outgoingMessageStatus, State currentState) {
         return super.processOutgoingMessageStatus(outgoingMessageStatus, currentState);
     }

     // To process incoming calls
     @Override
     public State processIncomingCall(IncomingCall incomingCall, State currentState) {
         return super.processIncomingCall(incomingCall, currentState);
     }

     // To handle chat blocking
     @Override
     public State processIncomingBlock(IncomingBlock incomingBlock, State currentState) {
         return super.processIncomingBlock(incomingBlock, currentState);
     }
}
```

### Receive webhooks via HTTP API

You can get incoming webhooks (messages, statuses) via HTTP API requests in the similar way as the rest of the Green API methods are implemented. Herewith, the chronological order of the webhooks following is guaranteed in the sequence in which they were received FIFO. All incoming webhooks are stored in the queue and are expected to be received within 24 hours.

To get incoming webhooks, you have to sequentially call two methods <a href="https://green-api.com/en/docs/api/receiving/technology-http-api/ReceiveNotification/">ReceiveNotification</a> and <a href="https://green-api.com/en/docs/api/receiving/technology-http-api/DeleteNotofication/">DeleteNotification</a>. ReceiveNotification method receives an incoming webhook. DeleteNotification method confirms successful webhook receipt and processing. To learn more about the methods, refer to respective ReceiveNotification and DeleteNotification sections.

### How to filter incoming messages

Filtering by webhook type occurs automatically by overriding the necessary methods, as described in the paragraph above, but how to filter messages?

Since each notification is automatically cast to a java object, you can filter messages by any field of the object yourself.
A description of the structure of notification objects can be found at this link: [Documentation](https://greenapi.com/en/docs/api/receiving/notifications-format/type-webhook/)
For convenience, all java objects and fields are named similarly to the documentation:

| Java object                           | Webhook's json object                                                                                                                                       |
|---------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `TextMessageWebhook`                  | [TextMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/TextMessage/)                                                |
| `TemplateMessageWebhook`              | [TemplateMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/TemplateMessage/)                                        |
| `StickerMessageWebhook`               | [StickerMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/StickerMessage/)                                          |
| `ReactionMessageWebhook`              | [ReactionMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ReactionMessage/)                                        |
| `QuotedMessageWebhook`                | [QuotedMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/QuotedMessage/)                                            |
| `PollUpdateMessageWebhook`            | [PollUpdateMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/PollUpdateMessage/)                                    |
| `PollMessageWebhook`                  | [PollMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/PollMessage/)                                                |
| `LocationMessageWebhook`              | [LocationMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/LocationMessage/)                                        |
| `ListMessageWebhook`                  | [ListMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ListMessage/)                                                |
| `GroupInviteMessageWebhook`           | [GroupInviteMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/GroupInviteMessage/)                                  |
| `FileMessageWebhook`                  | [imageMessage, videoMessage, documentMessage, audioMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ImageMessage/) |
| `ExtendedTextMessageWebhook`          | [ExtendedTextMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ExtendedTextMessage/)                                |
| `ButtonsMessageWebhook`               | [ButtonsMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ButtonsMessage/)                                          |
| `ContactMessageWebhook`               | [ContactMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ContactMessage/)                                          |
| `ContactsArrayMessageWebhook`         | [ContactMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/incoming-message/ContactsArrayMessage/)                                    |
| `TemplateButtonsReplyMessageWebhook`  | [TemplateButtonsReplyMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/selected-buttons/TemplateButtonsReplyMessage/)                |
| `ButtonsResponseMessageWebhook`       | [ButtonsResponseMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/selected-buttons/ButtonsResponseMessage/)                          |
| `ListResponseMessageWebhook`          | [ListResponseMessage](https://greenapi.com/en/docs/api/receiving/notifications-format/selected-buttons/ListResponseMessage/)                                |

You can independently check and convert the received notification to the type you need.

Link to example: [MediaStartScene.java](src/main/java/com/greenapi/chatbot/examples/media/MediaStartScene.java).

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

Also, for convenience and to improve code readability, the methods of the `Scene` class are overloaded; they have built-in the most popular filters for the text of the incoming message.
Examples of using methods of this class will be described below. If you want to make a condition without executing the methods of the `Scene` class,
you can use the methods of the `Filter` class of this library, which return a `boolean` value:

| Filter Method                                                                         | Description                                                                        |
|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| `Filter.isSenderIdExpected(MessageWebhook messageWebhook, String expectedSenderId)`   | Returns `true` if `expectedSenderId` is equal to the sender ID in `messageWebhook` |
| `Filter.isMessageTextRegex(MessageWebhook messageWebhook, Pattern regexPattern)`      | Returns `true` if `regexPattern` matches the text in `messageWebhook`              |
| `Filter.isMessageTextExpected(MessageWebhook messageWebhook, String expectedMessage)` | Returns `true` if `expectedMessage` equals the text in `messageWebhook`            |

#### Example

In this example, the bot will send a message and a file in response to the `rates` command.
Sending a message using the `answerWithText()` method is triggered only in response to the `rates` command due to the fact that the third parameter to the `answerWithText` method
the string `"rates"` is passed. This mechanism is implemented in all methods of the `Scene` class. If you want to reply to all messages, without a filter, simply omit the third parameter.
You can also pass a regex pattern as the third parameter instead of a string.

Sending a file using the `answerWithUploadFile()` method is triggered only in response to the `rates` command due to the fact that the method is located in the `if` block in the condition of which the method is executed
`Filter.isMessageTextExpected(incomingMessage, "rates")`.

Link to example: [FiltersStartScene.java](src/main/java/com/greenapi/chatbot/examples/filters/FiltersStartScene.java).

```java
public class FiltersStartScene extends Scene {
     @Override
     public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

         answerWithText(incomingMessage, "You see this because you wrote \"rates\"", "rates"); //filtering by overloaded method

         if (Filter.isMessageTextExpected(incomingMessage, "rates")) { //filtering using the Filter class method
             answerWithUploadFile(incomingMessage, new File("src/main/resources/data/rates.png"));
         }

         return currentState;
     }
}
```

### How to manage scenes

There are special methods in the `Scene` class to manage scenes. They must be executed after the `return` keyword inside the active scene.
These methods change the `scene` parameter in the chat state, the next message from the selected chat will go into a new scene.

| Scene Methods                                            | Description                                            |
|----------------------------------------------------------|--------------------------------------------------------|
| `activateNextScene(State currentState, Scene nextScene)` | Activates the next `nextScene` for the current chat.   |
| `activateStartScene(State currentState)`                 | Activates the start scene for the current user.        |

A complete list of methods available on the stage is described at the end of the file.

### How to manage user state

To control the user's state, simply make changes to the `currentState` object inside the scene and return it using `return`.
At the end of each scene, the state is automatically updated.

To manage state directly, you need to use the `stateManager` object, which is an instance of your implementation of the `StateManager` interface.
This object is available in any scene, as it is one of its fields.
The manager has methods that perform basic crud operations on the state. You also have the option to save chat data in its state.

| StateManager Methods | Description                                                                                  |
|----------------------|----------------------------------------------------------------------------------------------|
| `get()`              | Returns the state of the selected chat.                                                      |
| `create()`           | Creates a new state for chat.                                                                |
| `update()`           | Updates the state (the method is not present in the default implementation of the interface) |
| `delete()`           | Removes the user's state.                                                                    |
| `getStateData()`     | Returns state data                                                                           |
| `setStateData()`     | If the state exists, overwrites the state data                                               |
| `updateStateData()`  | If the state exists, updates the state data (put)                                            |
| `deleteStateData()`  | If the state exists, then clears the state data (sets default values)                        |

>The state identifier is the chat ID (the chatId field, not to be confused with senderId).

As an example, a bot was created to register a user.

Link to example: [state](src/main/java/com/greenapi/chatbot/examples/state).

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

### Bot example

As an example, a bot was created that demonstrates sending methods of the Scene class.

The bot is started with the command - /start
After launching, you need to select a method from the menu, and the bot will execute it.

Link to example: [full](src/main/java/com/greenapi/chatbot/examples/full).

The start scene waits for the `/start` command, after which it sends the menu and activates the next `ChooseScene`.

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

The second scene waits for the user's response and starts the execution of the desired method.
If `answerWithUrlFile()` is selected, the `InputLinkScene` scene is launched

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

This scene waits for the user to send a link to the file.
If the link is correct, sends the file and returns to the `ChooseScene`.

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

### List of Scene class methods

| Scene's Methods                                                                                                             | Description                                                                                    |
|-----------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| `activateNextScene(State currentState, Scene nextScene)`                                                                    | Activates the next `nextScene` for the current chat.                                           |
| `activateStartScene(State currentState)`                                                                                    | Activates the start scene for the current user.                                                |
| `getText(MessageWebhook messageWebhook)`                                                                                    | Returns the text of the message if it is text, otherwise returns `null`                        |
| `answerWithText(MessageWebhook messageWebhook, String text)`                                                                | Replies with text to an incoming message.                                                      |
| `answerWithUploadFile(MessageWebhook messageWebhook, String caption, File file)`                                            | Downloads and sends a file in response to an incoming message. `Caption` is an optional field. |
| `answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String fileName)`                             | Sends a file from a url in response to an incoming message. `Caption` is an optional field.    |
| `answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude)` | Sends geolocation in response to an incoming message.                                          |
| `answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers)`              | Sends a poll in response to an incoming message.                                               |
| `answerWithContact(MessageWebhook messageWebhook, Contact contact)`                                                         | Sends a contact in response to an incoming message.                                            |

> In the overloaded version, message response methods may contain additional parameters `expectedMessage` and `regexPattern`.
> If the text of the incoming message matches the condition, the method will be executed and return the method response according to the documentation, if not, then the method will return `null`.

## Documentation on service methods

[Documentation on service methods](https://greenapi.com/en/docs/api/)

## License

Licensed under [
Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
](https://creativecommons.org/licenses/by-nd/4.0/).
[LICENSE](LICENSE).
