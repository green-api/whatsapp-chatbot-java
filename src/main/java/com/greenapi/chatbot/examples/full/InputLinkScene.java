package com.greenapi.chatbot.examples.full;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InputLinkScene extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var incomingText = getText(incomingMessage);

        if (incomingText != null) {
            answerWithUrlFile(incomingMessage, "This is your file!", incomingText, "testFile");

        } else {
            answerWithText(incomingMessage, "Please send me a link!");

            return currentState;
        }

        return activateNextScene(currentState, new ChooseScene());
    }
}
