package com.greenapi.chatbot.pkg.calls;

import java.time.Duration;

public final class BotCallsDefaults {
    public static final Duration ANSWER_TIMEOUT = Duration.ofSeconds(60);
    public static final String THREAD_NAME = "bot-calls";

    private BotCallsDefaults() {
    }
}
