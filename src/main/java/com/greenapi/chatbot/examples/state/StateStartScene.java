package com.greenapi.chatbot.examples.state;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

public class StateStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        answerWithText(incomingMessage, "Hello. Tell me your username.");

        return activateNextScene(currentState, new InputUsernameScene());
    }
}
