package com.greenapi.chatbot.pkg.calls;

import com.greenapi.client.pkg.api.calls.CallsConnection;

@FunctionalInterface
public interface CallConversation {
    void talk(CallsConnection calls) throws Exception;
}
