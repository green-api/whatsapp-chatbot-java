package com.greenapi.chatbot;

import com.greenapi.chatbot.examples.echo.EchoStartScene;
import com.greenapi.chatbot.examples.media.MediaStartScene;
import com.greenapi.chatbot.examples.state.StateStartScene;
import com.greenapi.chatbot.pkg.BotFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhatsappChatbotJavaApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(WhatsappChatbotJavaApplication.class, args);
        var botFactory = context.getBean(BotFactory.class);
        var handlerExample = context.getBean(HandlerExample.class);

        var bot = botFactory.createBot(
            "1101848919",
            "fe0453b47e1b403c8d88ce881291ea002292b3037ae045bcb2",
            handlerExample,
            new MediaStartScene());

        bot.startReceivingNotifications();
    }
}
