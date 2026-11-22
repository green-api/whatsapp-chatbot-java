package com.greenapi.chatbot.pkg.calls;

import com.greenapi.client.pkg.api.GreenApi;
import com.greenapi.client.pkg.api.calls.CallsConnection;
import com.greenapi.client.pkg.api.exceptions.CallErrorCode;
import com.greenapi.client.pkg.api.exceptions.CallException;
import com.greenapi.client.pkg.models.calls.CallStateKind;
import com.greenapi.client.pkg.models.request.CallsDialReq;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.greenapi.chatbot.pkg.calls.BotCallsDefaults.ANSWER_TIMEOUT;
import static com.greenapi.chatbot.pkg.calls.BotCallsDefaults.THREAD_NAME;

@Log4j2
public class BotCalls implements AutoCloseable {
    private final GreenApi greenApi;
    private CallsConnection connection;
    private ExecutorService conversations;

    public BotCalls(GreenApi greenApi) {
        this.greenApi = greenApi;
    }

    public synchronized CallsConnection connection() {
        if (connection == null) {
            connection = greenApi.calls.connectCalls();
        }
        connection.open();
        return connection;
    }

    public String answer(CallConversation conversation) {
        var calls = connection();
        var callId = greenApi.calls.callsAccept().getBody().getCallId();
        talk(calls, conversation);
        return callId;
    }

    public String dial(String chatId, CallConversation conversation) {
        var calls = connection();
        var callId = greenApi.calls.callsDial(new CallsDialReq(chatId)).getBody().getCallId();
        talk(calls, conversation);
        return callId;
    }

    public String reject() {
        return greenApi.calls.callsReject().getBody().getCallId();
    }

    public String hangUp() {
        return greenApi.calls.callsHangUp().getBody().getCallId();
    }

    @Override
    public synchronized void close() {
        if (conversations != null) {
            conversations.shutdownNow();
            conversations = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    private synchronized ExecutorService conversations() {
        if (conversations == null) {
            conversations = Executors.newSingleThreadExecutor(runnable -> {
                var thread = new Thread(runnable, THREAD_NAME);
                thread.setDaemon(true);
                return thread;
            });
        }
        return conversations;
    }

    private void talk(CallsConnection calls, CallConversation conversation) {
        conversations().execute(() -> {
            try {
                calls.startAudioBridge();
                calls.waitFor(CallStateKind.ON_CALL, ANSWER_TIMEOUT);
                conversation.talk(calls);
            } catch (CallException e) {
                if (e.getCode() != CallErrorCode.CALL_ENDED) {
                    log.error("Call failed: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Call conversation failed: " + e.getMessage());
            } finally {
                endCall(calls);
            }
        });
    }

    private void endCall(CallsConnection calls) {
        var state = calls.getState();
        if (state == null || state.getState() == CallStateKind.IDLE) {
            return;
        }
        try {
            greenApi.calls.callsHangUp();
        } catch (CallException e) {
            if (e.getCode() != CallErrorCode.NO_ACTIVE_CALL) {
                log.error("Hang up failed: " + e.getMessage());
            }
        }
    }
}
