package com.greenapi.chatbot.examples;

import com.greenapi.chatbot.examples.full.FullStartScene;
import com.greenapi.chatbot.pkg.BotFactory;
import org.springframework.boot.SpringApplication;

//@SpringBootApplication
public class BotStarterClassExample {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarterClassExample.class, args);
        var botFactory = context.getBean(BotFactory.class);
        var handlerExample = context.getBean(HandlerExample.class);

        var bot = botFactory.createBot(
            "1101848919",
            "fe0453b47e1b403c8d88ce881291ea002292b3037ae045bcb2",
            handlerExample,
            new FullStartScene());

        bot.startReceivingNotifications();
    }
}
