package com.greenapi.chatbot.examples.event;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.IncomingBlock;
import com.greenapi.client.pkg.models.notifications.IncomingCall;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.notifications.OutgoingMessageStatus;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EventStartScene extends Scene {

    //  Для обработки входящих сообщений.
    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info(incomingMessage);

        return currentState;
    }

    //  Для обработки исходящих сообщений
    @Override
    public State processOutgoingMessage(MessageWebhook outgoingMessage, State currentState) {
        return super.processOutgoingMessage(outgoingMessage, currentState);
    }

    //  Для обработки статусов исходящих сообщений
    @Override
    public State processOutgoingMessageStatus(OutgoingMessageStatus outgoingMessageStatus, State currentState) {
        return super.processOutgoingMessageStatus(outgoingMessageStatus, currentState);
    }

    //  Для обработки входящих звонков
    @Override
    public State processIncomingCall(IncomingCall incomingCall, State currentState) {
        return super.processIncomingCall(incomingCall, currentState);
    }

    //  Для обработки блокировок чата
    @Override
    public State processIncomingBlock(IncomingBlock incomingBlock, State currentState) {
        return super.processIncomingBlock(incomingBlock, currentState);
    }
}
