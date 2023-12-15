package com.greenapi.chatbot.examples.base;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.MapState;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

public class BaseStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        answerWithText(incomingMessage, "Hello!", "message");

        return currentState;
    }
}
