package com.greenapi.chatbot.example_bot_scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.filters.TypeFilter;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.Contact;
import com.greenapi.client.pkg.models.Option;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

@Log4j2
public class MethodChooseSceneExample extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info("MethodChooseSceneExample: start");

        if (TypeFilter.isMessageTextExpected(incomingMessage, "1")) {
            answerWithText(incomingMessage, "Hi! This is answerWithText!");

            log.info("MethodChooseSceneExample: 1");
            return currentState;

        } else if (TypeFilter.isMessageTextExpected(incomingMessage, "2")) {
            answerWithText(incomingMessage, "Send me the link on File:");

            log.info("MethodChooseSceneExample: 2");
            return activateNextScene(currentState, new AnswerWithFileSceneExample());

        } else if (TypeFilter.isMessageTextExpected(incomingMessage, "3")) {
            var options = new ArrayList<Option>();
            options.add(new Option("Red"));
            options.add(new Option("Blue"));
            options.add(new Option("Green"));
            options.add(new Option("Pink"));

            answerWithPoll(incomingMessage, "choose color", options, false);

            log.info("MethodChooseSceneExample: 3");
            return currentState;

        } else if (TypeFilter.isMessageTextExpected(incomingMessage, "4")) {
            answerWithLocation(incomingMessage, "Home", "Cdad. de La Paz 2969, Buenos Aires", -34.5553558, -58.4642510);

            log.info("MethodChooseSceneExample: 4");
            return currentState;

        } else if (TypeFilter.isMessageTextExpected(incomingMessage, "5")) {
            var contact = Contact.builder()
                .firstName("first")
                .lastName("last")
                .middleName("middle")
                .company("Green API")
                .phoneContact(11111111111L)
                .build();

            answerWithContact(incomingMessage, contact);

            log.info("MethodChooseSceneExample: 5");
            return currentState;

        } else if (TypeFilter.isMessageTextExpected(incomingMessage, "6")) {

            answerWithText(incomingMessage, "Bie!");

            log.info("MethodChooseSceneExample: exit");
            return activateStartScene(currentState);
        }

        answerWithText(incomingMessage, "Please send numbers - 1, 2, 3, 4, 5 or 6");
        return currentState;
    }
}
