package com.greenapi.chatbot.examples.media;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.ContactMessageWebhook;
import com.greenapi.client.pkg.models.notifications.FileMessageWebhook;
import com.greenapi.client.pkg.models.notifications.LocationMessageWebhook;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

public class MediaStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        if (incomingMessage instanceof ContactMessageWebhook) {
            answerWithText(incomingMessage, "This is a contact message");

        } else if (incomingMessage instanceof LocationMessageWebhook) {
            answerWithText(incomingMessage, "This is location message");

        } else if (incomingMessage instanceof FileMessageWebhook) {
            answerWithText(incomingMessage, "This is a message with a file");
        }

        return currentState;
    }
}
