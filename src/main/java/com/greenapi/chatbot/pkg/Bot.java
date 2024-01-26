package com.greenapi.chatbot.pkg;

import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.client.pkg.api.GreenApi;
import com.greenapi.client.pkg.api.webhook.NotificationMapper;
import com.greenapi.client.pkg.api.webhook.WebhookConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Log4j2
public class Bot {
    private Boolean cleanNotificationQueue;
    private WebhookConsumer webhookConsumer;
    private NotificationMapper notificationMapper;
    private BotHandler botHandler;
    private Scene startScene;
    public GreenApi greenApi;
    private StateManager stateManager;

    public void startReceivingNotifications() {
        if (cleanNotificationQueue) {
            deleteAllNotifications();
        }
        webhookConsumer.start((notification) -> {
            botHandler.handle(notification);
        });
    }

    public void stopReceivingNotifications() {
        webhookConsumer.stop();
        log.info("Receiving stopped");
    }

    @SneakyThrows
    private void deleteAllNotifications() {
        boolean cleaning = true;

        log.info("deleting notifications...");
        while (cleaning) {
            try {
                var response = greenApi.receiving.receiveNotification();

                if (Objects.equals(response.getBody(), "null")) {
                    cleaning = false;
                    log.info("deleting notifications finished!");

                } else {
                    var notification = notificationMapper.get(response.getBody());
                    greenApi.receiving.deleteNotification(notification.getReceiptId());
                }
            } catch (Exception e) {
                log.error("Unexpected error: " + e.getMessage());
                Thread.sleep(5000);
            }
        }
    }

    public void setStartScene(Scene scene) {
        this.startScene = scene;

        startScene.setGreenApi(greenApi);
        startScene.setStateManager(stateManager);
        botHandler.startScene = startScene;
    }
}
