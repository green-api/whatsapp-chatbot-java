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
import java.util.Optional;
import java.util.regex.Pattern;

import static com.greenapi.chatbot.pkg.filters.Filter.isMessageTextExpected;
import static com.greenapi.chatbot.pkg.filters.Filter.isMessageTextRegex;

@Log4j2
public abstract class Scene {
    protected GreenApi greenApi;
    protected StateManager stateManager;

    public final void setGreenApi(GreenApi greenApi) {
        this.greenApi = greenApi;
    }

    public final void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
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
        nextScene.setStateManager(stateManager);
        nextScene.setGreenApi(greenApi);
        currentState.setScene(nextScene);

        return currentState;
    }

    protected final State activateStartScene(State currentState) {
        currentState.setScene(null);

        return currentState;
    }

    protected static Optional<String> getText(MessageWebhook messageWebhook) {
        if (messageWebhook instanceof TextMessageWebhook msg) {
            return msg.getMessageData().getTextMessageData().getTextMessage().describeConstable();

        } else if (messageWebhook instanceof ExtendedTextMessageWebhook msg) {
            return msg.getMessageData().getExtendedTextMessageData().getText().describeConstable();
        }

        return Optional.empty();
    }


    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, Boolean isQuote) throws BotRequestException {
        var chatId = messageWebhook.getSenderData().getChatId();

        var responseEntity = greenApi.sending.sendMessage(
            OutgoingMessage.builder()
                .chatId(chatId)
                .message(text)
                .quotedMessageId(isQuote ? messageWebhook.getIdMessage() : null)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text) throws BotRequestException {
        return answerWithText(messageWebhook, text, true);
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, String expectedMessage, Boolean isQuote) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithText(messageWebhook, text, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, Pattern regexPattern, Boolean isQuote) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithText(messageWebhook, text, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, String expectedMessage) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithText(messageWebhook, text, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithText(MessageWebhook messageWebhook, String text, Pattern regexPattern) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithText(messageWebhook, text, true);
        }
        return null;
    }


    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, String caption, File file, Boolean isQuote) throws BotRequestException {
        var sender = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendFileByUpload(
            OutgoingFileByUpload.builder()
                .chatId(sender)
                .file(file)
                .fileName(file.getName())
                .caption(caption)
                .quotedMessageId(isQuote ? messageWebhook.getIdMessage() : null)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, Boolean isQuote) throws BotRequestException {
        return answerWithUploadFile(messageWebhook, null, file, isQuote);
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, String caption, Boolean isQuote) throws BotRequestException {
        return answerWithUploadFile(messageWebhook, caption, file, isQuote);
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, String caption, String expectedMessage, Boolean isQuote) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithUploadFile(messageWebhook, caption, file, isQuote);
        }
        return null;
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, String caption, Pattern regexPattern, Boolean isQuote) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithUploadFile(messageWebhook, caption, file, isQuote);
        }
        return null;
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file) throws BotRequestException {
        return answerWithUploadFile(messageWebhook, null, file, true);
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, String caption) throws BotRequestException {
        return answerWithUploadFile(messageWebhook, caption, file, true);
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, String caption, String expectedMessage) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithUploadFile(messageWebhook, caption, file, true);
        }
        return null;
    }

    protected final SendFileByUploadResp answerWithUploadFile(MessageWebhook messageWebhook, File file, String caption, Pattern regexPattern) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithUploadFile(messageWebhook, caption, file, true);
        }
        return null;
    }


    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String fileName, Boolean isQuote) throws BotRequestException {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendFileByUrl(
            OutgoingFileByUrl.builder()
                .chatId(chatId)
                .urlFile(url)
                .fileName(fileName)
                .caption(caption)
                .quotedMessageId(isQuote ? messageWebhook.getIdMessage() : null)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String url, String filename, Boolean isQuote) throws BotRequestException {
        return answerWithUrlFile(messageWebhook, null, url, filename, isQuote);
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String filename, String expectedMessage, Boolean isQuote) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithUrlFile(messageWebhook, caption, url, filename, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String filename, Pattern regexPattern, Boolean isQuote) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithUrlFile(messageWebhook, caption, url, filename, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String url, String filename) throws BotRequestException {
        return answerWithUrlFile(messageWebhook, null, url, filename, true);
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String filename) throws BotRequestException {
        return answerWithUrlFile(messageWebhook, caption, url, filename, true);
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String filename, String expectedMessage) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithUrlFile(messageWebhook, caption, url, filename, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithUrlFile(MessageWebhook messageWebhook, String caption, String url, String filename, Pattern regexPattern) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithUrlFile(messageWebhook, caption, url, filename, true);
        }
        return null;
    }


    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude, Boolean isQuote) throws BotRequestException {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendLocation(
            OutgoingLocation.builder()
                .chatId(chatId)
                .nameLocation(nameLocation)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .quotedMessageId(isQuote ? messageWebhook.getIdMessage() : null)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude) throws BotRequestException {
        return answerWithLocation(messageWebhook, nameLocation, address, latitude, longitude, true);
    }

    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude, String expectedMessage) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithLocation(messageWebhook, nameLocation, address, latitude, longitude, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude, Pattern regexPattern) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithLocation(messageWebhook, nameLocation, address, latitude, longitude, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude, String expectedMessage, Boolean isQuote) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithLocation(messageWebhook, nameLocation, address, latitude, longitude, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithLocation(MessageWebhook messageWebhook, String nameLocation, String address, Double latitude, Double longitude, Pattern regexPattern, Boolean isQuote) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithLocation(messageWebhook, nameLocation, address, latitude, longitude, isQuote);
        }
        return null;
    }


    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers, Boolean isQuote) throws BotRequestException {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendPoll(
            OutgoingPoll.builder()
                .chatId(chatId)
                .options(options)
                .multipleAnswers(multipleAnswers)
                .quotedMessageId(isQuote ? messageWebhook.getIdMessage() : null)
                .message(message)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers) throws BotRequestException {
        return answerWithPoll(messageWebhook, message, options, multipleAnswers, true);
    }

    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers, String expectedMessage, Boolean isQuote) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithPoll(messageWebhook, message, options, multipleAnswers, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers, Pattern regexPattern, Boolean isQuote) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithPoll(messageWebhook, message, options, multipleAnswers, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers, String expectedMessage) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedMessage)) {
            return answerWithPoll(messageWebhook, message, options, multipleAnswers, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithPoll(MessageWebhook messageWebhook, String message, List<Option> options, Boolean multipleAnswers, Pattern regexPattern) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithPoll(messageWebhook, message, options, multipleAnswers, true);
        }
        return null;
    }


    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact, Boolean isQuote) throws BotRequestException {
        var chatId = messageWebhook.getSenderData().getChatId();
        var responseEntity = greenApi.sending.sendContact(
            OutgoingContact.builder()
                .chatId(chatId)
                .contact(contact)
                .quotedMessageId(isQuote ? messageWebhook.getIdMessage() : null)
                .build());

        if (responseEntity.getStatusCode().isError()) {
            throw new BotRequestException(responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact) throws BotRequestException {
            return answerWithContact(messageWebhook, contact, true);
    }

    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact, String expectedString) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedString)) {
            return answerWithContact(messageWebhook, contact, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact, Pattern regexPattern) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithContact(messageWebhook, contact, true);
        }
        return null;
    }

    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact, String expectedString, Boolean isQuote) throws BotRequestException {
        if (isMessageTextExpected(messageWebhook, expectedString)) {
            return answerWithContact(messageWebhook, contact, isQuote);
        }
        return null;
    }

    protected final SendMessageResp answerWithContact(MessageWebhook messageWebhook, Contact contact, Pattern regexPattern, Boolean isQuote) throws BotRequestException {
        if (isMessageTextRegex(messageWebhook, regexPattern)) {
            return answerWithContact(messageWebhook, contact, isQuote);
        }
        return null;
    }
}
