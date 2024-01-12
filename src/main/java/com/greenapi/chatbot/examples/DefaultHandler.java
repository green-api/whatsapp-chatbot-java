package com.greenapi.chatbot.examples;

import com.greenapi.chatbot.pkg.BotHandler;
import com.greenapi.client.pkg.models.notifications.DeviceInfo;
import com.greenapi.client.pkg.models.notifications.StateInstanceChanged;

public class DefaultHandler extends BotHandler {

    @Override
    public void processStateInstanceChanged(StateInstanceChanged stateInstanceChanged) {
        super.processStateInstanceChanged(stateInstanceChanged);
    }

    @Override
    public void processDeviceInfo(DeviceInfo deviceInfo) {
        super.processDeviceInfo(deviceInfo);
    }
}
