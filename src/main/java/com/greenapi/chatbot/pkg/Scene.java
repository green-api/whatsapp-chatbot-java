package com.greenapi.chatbot.pkg;

import com.greenapi.chatbot.pkg.exception.BotRequestException;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.client.pkg.api.GreenApi;
import com.greenapi.client.pkg.models.Contact;
import com.greenapi.client.pkg.models.Option;
import com.greenapi.client.pkg.models.notifications.IncomingBlock;
import com.greenapi.client.pkg.models.notifications.IncomingCall;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.notifications.OutgoingMessageStatus;
import com.greenapi.client.pkg.models.request.*;
import com.greenapi.client.pkg.models.response.SendFileByUploadResp;
import com.greenapi.client.pkg.models.response.SendMessageResp;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static com.greenapi.chatbot.pkg.filters.TypeFilter.*;

@Data
@Log4j2
public abstract class Scene {

    private GreenApi greenApi;
    private StateManager stateManager;

    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        return currentState;
    }

    public State processOutgoingMessage(MessageWebhook outgoingMessage, State currentState) {
        return currentState;
    }

    public State processOutgoingMessageStatus(OutgoingMessageStatus outgoingMessageStatus, State currentState) {
        return currentState;
    }

    public State processIncomingCall(IncomingCall incomingCall, State currentState) {
        return currentState;
    }

    public State processIncomingBlock(IncomingBlock incomingBlock, State currentState) {
        return currentState;
    }

    protected State activateNextScene(State currentState, Scene nextScene) {
        var updatedData = currentState.getData();
        nextScene.setStateManager(stateManager);
        nextScene.setGreenApi(greenApi);
        updatedData.put("scene", nextScene);
        currentState.setData(updatedData);

        return currentState;
    }

    protected State activateStartScene(State currentState) {
        var updatedData = currentState.getData();
        updatedData.put("scene", null);
        currentState.setData(updatedData);

        return currentState;
    }

    public SendMessageResp answerWithText(MessageWebhook messageWebhook, String text) {
        var chatId = messageWebhook.getSenderData().getChatId();
        log.info("incoming message: " + messageWebhook.getIdMessage());

        var responseEntity = greenApi.sending.sendMessage(
            OutgoingMessage.builder()
                .chatId(chatId)
                .message(text)
                .quotedMessageId(messageWebhook.getIdMessage())
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }

    public SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, String expectedMessage) {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithText(messageWebhook, text);
        }

        return null;
    }

    public SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, Pattern regexPattern) {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithText(messageWebhook, text);
        }

        return null;
    }

    public SendFileByUploadResp answerWithUploadFile(String caption, File file, MessageWebhook messageWebhook) {
        var sender = messageWebhook.getSenderData().getSender();
        var responseEntity = greenApi.sending.sendFileByUpload(
            OutgoingFileByUpload.builder()
                .chatId(sender)
                .file(file)
                .fileName(file.getName())
                .caption(caption)
                .quotedMessageId(messageWebhook.getIdMessage())
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }

    public SendFileByUploadResp answerWithUploadFile(File file, MessageWebhook messageWebhook) {

        return answerWithUploadFile(null, file, messageWebhook);
    }

    public SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook,
                                             String caption,
                                             String url,
                                             String fileName) {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendFileByUrl(
            OutgoingFileByUrl.builder()
                .chatId(chatId)
                .urlFile(url)
                .fileName(fileName)
                .caption(caption)
                .quotedMessageId(messageWebhook.getIdMessage())
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }

    public SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String url, String filename) {

        return answerWithUrlFile(messageWebhook, null, url, filename);
    }

    public SendMessageResp answerWithLocation(MessageWebhook messageWebhook,
                                              String nameLocation,
                                              String address,
                                              Double latitude,
                                              Double longitude) {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendLocation(
            OutgoingLocation.builder()
                .chatId(chatId)
                .nameLocation(nameLocation)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .quotedMessageId(messageWebhook.getIdMessage())
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }

    public SendMessageResp answerWithPoll(MessageWebhook messageWebhook,
                                          String message,
                                          List<Option> options,
                                          Boolean multipleAnswers) {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendPoll(
            OutgoingPoll.builder()
                .chatId(chatId)
                .options(options)
                .multipleAnswers(multipleAnswers)
                .quotedMessageId(messageWebhook.getIdMessage())
                .message(message)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }

    public SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact) {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendContact(
            OutgoingContact.builder()
                .chatId(chatId)
                .contact(contact)
                .quotedMessageId(messageWebhook.getIdMessage())
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }
}
