package com.greenapi.chatbot.examples.calls;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.IncomingCall;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.notifications.OutgoingCall;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.util.regex.Pattern;

@Log4j2
public class CallsStartScene extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        if (answerWithText(incomingMessage, "Calling you", Pattern.compile("^call me$", Pattern.CASE_INSENSITIVE)) != null) {
            callChat(incomingMessage.getSenderData().getChatId(),
                calls -> calls.play(Path.of("src/main/resources/data/greeting.wav")));
        }

        return currentState;
    }

    @Override
    public State processIncomingCall(IncomingCall incomingCall, State currentState) {
        log.info("Incoming call from " + incomingCall.getFrom() + ": " + incomingCall.getStatus());

        if (incomingCall.getStatus().equals("offer")) {
            answerCall(calls -> calls.play(Path.of("src/main/resources/data/greeting.wav")));
        }

        return currentState;
    }

    @Override
    public State processOutgoingCall(OutgoingCall outgoingCall, State currentState) {
        log.info("Outgoing call to " + outgoingCall.getFrom() + ": " + outgoingCall.getStatus());

        return currentState;
    }
}
