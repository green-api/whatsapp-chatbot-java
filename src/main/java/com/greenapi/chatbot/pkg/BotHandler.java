package com.greenapi.chatbot.pkg;

import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.client.pkg.api.GreenApi;
import com.greenapi.client.pkg.api.webhook.WebhookHandler;
import com.greenapi.client.pkg.models.notifications.*;
import lombok.extern.log4j.Log4j2;

import static com.greenapi.chatbot.pkg.filters.Filter.*;

@Log4j2
public abstract class BotHandler implements WebhookHandler {

    protected GreenApi greenApi;
    protected StateManager stateManager;
    protected Scene startScene;

    @Override
    public void handle(Notification notification) {

        var notificationBody = notification.getBody();

        try {

            if (isIncomingMessageReceived(notificationBody)) {
                var messageWebhook = (MessageWebhook) notificationBody;
                var stateId = messageWebhook.getSenderData().getChatId();

                var currentState = stateManager.get(stateId).orElse(stateManager.create(stateId));
                var scene = currentState.getScene();

                if (scene == null) {
                    scene = startScene;
                }

                var updatedState = scene.processIncomingMessage(messageWebhook, currentState);
                stateManager.updateStateData(stateId, updatedState.getData());

            } else if (isOutgoingMessageReceived(notificationBody)) {
                var messageWebhook = (MessageWebhook) notificationBody;
                var stateId = messageWebhook.getSenderData().getChatId();

                var currentState = stateManager.get(stateId).orElse(stateManager.create(stateId));
                var scene = currentState.getScene();

                if (scene == null) {
                    scene = startScene;
                }

                var updatedState = scene.processOutgoingMessage(messageWebhook, currentState);
                stateManager.updateStateData(stateId, updatedState.getData());

            } else if (isOutgoingMessageStatus(notificationBody)) {
                var messageStatusWebhook = (OutgoingMessageStatus) notificationBody;
                var stateId = messageStatusWebhook.getChatId();

                var currentState = stateManager.get(stateId).orElse(stateManager.create(stateId));
                var scene = currentState.getScene();

                if (scene == null) {
                    scene = startScene;
                }

                var updatedState = scene.processOutgoingMessageStatus(messageStatusWebhook, currentState);
                stateManager.updateStateData(stateId, updatedState.getData());

            } else if (isStateInstanceChanged(notificationBody)) {
                processStateInstanceChanged((StateInstanceChanged) notificationBody);

            } else if (isIncomingCall(notificationBody)) {
                var incomingCall = (IncomingCall) notificationBody;
                var stateId = incomingCall.getFrom();

                var currentState = stateManager.get(stateId).orElse(stateManager.create(stateId));
                var scene = currentState.getScene();

                if (scene == null) {
                    scene = startScene;
                }

                var updatedState = scene.processIncomingCall((IncomingCall) notificationBody, currentState);
                stateManager.updateStateData(stateId, updatedState.getData());

            } else if (isIncomingBlock(notificationBody)) {
                var incomingBlock = (IncomingBlock) notificationBody;
                var stateId = incomingBlock.getChatId() + "@c.us";

                var currentState = stateManager.get(stateId).orElse(stateManager.create(stateId));
                var scene = currentState.getScene();

                if (scene == null) {
                    scene = startScene;
                }

                var updatedState = scene.processIncomingBlock((IncomingBlock) notificationBody, currentState);
                stateManager.updateStateData(stateId, updatedState.getData());

            } else if (isDeviceInfo(notificationBody)) {
                processDeviceInfo((DeviceInfo) notificationBody);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void processStateInstanceChanged(StateInstanceChanged stateInstanceChanged) {
    }

    public void processDeviceInfo(DeviceInfo deviceInfo) {
    }
}
