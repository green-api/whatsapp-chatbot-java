package com.greenapi.chatbot.example_bot_scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GreetingSceneExample extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info("GreetingSceneExample: start");

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
            log.info("GreetingSceneExample: isn't \"/start\"" + sendMessageResp);

            return currentState;
        }
        log.info("GreetingSceneExample: success: " + resp);

        return activateNextScene(currentState, new MethodChooseSceneExample());
    }
}
