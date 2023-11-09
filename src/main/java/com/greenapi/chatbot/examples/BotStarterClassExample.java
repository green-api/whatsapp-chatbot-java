package com.greenapi.chatbot.examples;

import com.greenapi.chatbot.examples.full.FullStartScene;
import com.greenapi.chatbot.pkg.BotFactory;
import org.springframework.boot.SpringApplication;

//@SpringBootApplication
public class BotStarterClassExample {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarterClassExample.class, args);
        var botFactory = context.getBean(BotFactory.class);

        var bot = botFactory.createBot(
            "{{instanceId}}",
            "{{token}}",
            new HandlerExample(),
            new FullStartScene());

        bot.startReceivingNotifications();
    }
}
