package com.greenapi.chatbot.pkg.exception;

import org.springframework.http.HttpStatusCode;

public class BotRequestException extends RuntimeException {
    public BotRequestException(HttpStatusCode statusCode) {
        super("Request is failed. Status code: " + statusCode);
    }

    public BotRequestException(String message) {
        super(message);
    }

    public BotRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
