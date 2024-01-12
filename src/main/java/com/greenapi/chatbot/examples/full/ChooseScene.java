package com.greenapi.chatbot.examples.full;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.Contact;
import com.greenapi.client.pkg.models.Option;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

import java.util.ArrayList;

public class ChooseScene extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        var text = getText(incomingMessage);

        if (text.isEmpty()) {
            answerWithText(incomingMessage, "Please send a text message!");
            return currentState;
        }

        switch (text.get()) {
            case "1" -> {
                answerWithText(incomingMessage, "Hi! This is answerWithText!");
                return currentState;
            }
            case "2" -> {
                answerWithText(incomingMessage, "Send me the link on File:");
                return activateNextScene(currentState, new InputLinkScene());
            }
            case "3" -> {
                var options = new ArrayList<Option>();
                options.add(new Option("Red"));
                options.add(new Option("Blue"));
                options.add(new Option("Green"));
                options.add(new Option("Pink"));
                answerWithPoll(incomingMessage, "choose color", options, false);
                return currentState;
            }
            case "4" -> {
                answerWithLocation(incomingMessage, "Home", "Cdad. de La Paz 2969, Buenos Aires", -34.5553558, -58.4642510);
                return currentState;
            }
            case "5" -> {
                var contact = Contact.builder()
                    .firstName("first")
                    .lastName("last")
                    .middleName("middle")
                    .company("Green API")
                    .phoneContact(11111111111L)
                    .build();
                answerWithContact(incomingMessage, contact);
                return currentState;
            }
            case "6" -> {
                answerWithText(incomingMessage, "Goodbye!");
                return activateStartScene(currentState);
            }
            default -> {
                answerWithText(incomingMessage, "Please send numbers - 1, 2, 3, 4, 5 or 6");
                return currentState;
            }
        }
    }
}
