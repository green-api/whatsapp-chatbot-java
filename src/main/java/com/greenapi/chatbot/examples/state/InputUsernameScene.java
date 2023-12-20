package com.greenapi.chatbot.examples.state;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

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
