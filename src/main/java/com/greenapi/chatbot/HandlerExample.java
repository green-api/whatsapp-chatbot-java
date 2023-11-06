package com.greenapi.chatbot;

import com.greenapi.chatbot.pkg.BotHandler;
import com.greenapi.client.pkg.models.notifications.DeviceInfo;
import com.greenapi.client.pkg.models.notifications.StateInstanceChanged;
import org.springframework.stereotype.Component;

@Component
public class HandlerExample extends BotHandler {

    @Override
    public void processStateInstanceChanged(StateInstanceChanged stateInstanceChanged) {
        super.processStateInstanceChanged(stateInstanceChanged);
    }

    @Override
    public void processDeviceInfo(DeviceInfo deviceInfo) {
        super.processDeviceInfo(deviceInfo);
    }
}
