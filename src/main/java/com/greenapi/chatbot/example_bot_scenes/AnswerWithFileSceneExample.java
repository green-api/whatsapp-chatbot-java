package com.greenapi.chatbot.example_bot_scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.ExtendedTextMessageWebhook;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.notifications.TextMessageWebhook;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AnswerWithFileSceneExample extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info("AnswerWithFileSceneExample: start");

        if (incomingMessage instanceof ExtendedTextMessageWebhook msg) {
            answerWithUrlFile(incomingMessage, "This is your file!", msg.getMessageData().getExtendedTextMessageData().getText(), "testFile");

            log.info("AnswerWithFileSceneExample: success UrlMessage");
            return activateNextScene(currentState, new MethodChooseSceneExample());

        } else if (incomingMessage instanceof TextMessageWebhook msg) {
            answerWithUrlFile(incomingMessage, "This is your file!", msg.getMessageData().getTextMessageData().getTextMessage(), "testFile");

            log.info("AnswerWithFileSceneExample: success TextMessage");
            return activateNextScene(currentState, new MethodChooseSceneExample());

        } else {
            answerWithText(incomingMessage, "Please send me a link!");

            return currentState;
        }
    }
}
