package com.greenapi.chatbot.examples.full;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.MapState;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

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
