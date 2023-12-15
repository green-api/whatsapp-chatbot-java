package com.greenapi.chatbot.examples.filters;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.filters.Filter;
import com.greenapi.chatbot.pkg.state.MapState;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

import java.io.File;

public class FiltersStartScene extends Scene {
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

        answerWithText(incomingMessage, "You see this because you wrote \"rates\"", "rates");

        if (Filter.isMessageTextExpected(incomingMessage, "rates")) {
            answerWithUploadFile(incomingMessage, new File("src/main/resources/data/rates.png"));
        }

        return currentState;
    }
}
