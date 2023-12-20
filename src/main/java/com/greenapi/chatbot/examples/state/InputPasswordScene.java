package com.greenapi.chatbot.examples.state;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

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
