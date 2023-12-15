package com.greenapi.chatbot.examples.echo;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.MapState;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

public class EchoStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var incomingText = getText(incomingMessage);

        if (incomingText != null) {
            answerWithText(incomingMessage, incomingText);
        }

        return currentState;
    }
}
