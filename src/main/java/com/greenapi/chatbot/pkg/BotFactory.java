package com.greenapi.chatbot.pkg;

import com.greenapi.chatbot.examples.DefaultHandler;
import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.client.pkg.api.GreenApi;
import com.greenapi.client.pkg.api.webhook.NotificationMapper;
import com.greenapi.client.pkg.api.webhook.WebhookConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BotFactory {
    private final RestTemplate restTemplate;
    private final StateManager stateManager;
    @Value("${green-api.hostMedia}")
    private String hostMedia = "https://media.green-api.com";
    @Value("${green-api.host}")
    private String host = "https://api.green-api.com";


    public Bot createBot(String instanceId, String instanceToken,
                         BotHandler handler, Boolean cleanNotificationQueue) {

        var greenApi = new GreenApi(restTemplate, hostMedia, host, instanceId, instanceToken);
        var notificationMapper = new NotificationMapper();
        var webhookConsumer = new WebhookConsumer(greenApi, notificationMapper);

        handler.greenApi = greenApi;
        handler.stateManager = stateManager;

        return Bot.builder()
            .greenApi(greenApi)
            .botHandler(handler)
            .notificationMapper(notificationMapper)
            .webhookConsumer(webhookConsumer)
            .cleanNotificationQueue(cleanNotificationQueue)
            .stateManager(stateManager)
            .build();
    }

    public Bot createBot(String instanceId, String instanceToken, BotHandler handler) {

        return createBot(instanceId, instanceToken, handler, true);
    }

    public Bot createBot(String instanceId, String instanceToken) {

        return createBot(instanceId, instanceToken, new DefaultHandler(), true);
    }
}
