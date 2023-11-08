package com.greenapi.chatbot.pkg;

import com.greenapi.chatbot.pkg.exception.BotRequestException;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.client.pkg.api.GreenApi;
import com.greenapi.client.pkg.models.Contact;
import com.greenapi.client.pkg.models.Option;
import com.greenapi.client.pkg.models.notifications.*;
import com.greenapi.client.pkg.models.request.*;
import com.greenapi.client.pkg.models.response.SendFileByUploadResp;
import com.greenapi.client.pkg.models.response.SendMessageResp;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static com.greenapi.chatbot.pkg.filters.Filter.isMessageTextExpected;
import static com.greenapi.chatbot.pkg.filters.Filter.isMessageTextRegex;

@Log4j2
public abstract class Scene {
    private GreenApi greenApi;
    private StateManager stateManager;

    public final GreenApi getGreenApi() {
        return greenApi;
    }

    public final StateManager getStateManager() {
        return stateManager;
    }

    public final void setGreenApi(GreenApi greenApi) {
        this.greenApi = greenApi;
    }

    public final void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public static String getText(MessageWebhook messageWebhook) {
        if (messageWebhook instanceof TextMessageWebhook msg) {
            return msg.getMessageData().getTextMessageData().getTextMessage();

        } else if (messageWebhook instanceof ExtendedTextMessageWebhook msg) {
            return msg.getMessageData().getExtendedTextMessageData().getText();
        }

        return null;
    }

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

    protected final State activateNextScene(State currentState, Scene nextScene) {
        var updatedData = currentState.getData();
        nextScene.setStateManager(stateManager);
        nextScene.setGreenApi(greenApi);
        updatedData.put("scene", nextScene);
        currentState.setData(updatedData);

        return currentState;
    }

    protected final State activateStartScene(State currentState) {
        var updatedData = currentState.getData();
        updatedData.put("scene", null);
        currentState.setData(updatedData);

        return currentState;
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text) {
        var chatId = messageWebhook.getSenderData().getChatId();

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

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, String expectedMessage) {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithText(messageWebhook, text);
        }

        return null;
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, Pattern regexPattern) {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithText(messageWebhook, text);
        }

        return null;
    }

    protected final SendFileByUploadResp answerWithUploadFile(String caption, File file, MessageWebhook messageWebhook) {
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

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file) {

        return answerWithUploadFile(null, file, messageWebhook);
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook,
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

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String url, String filename) {

        return answerWithUrlFile(messageWebhook, null, url, filename);
    }

    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook,
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

    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook,
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

    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact) {
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