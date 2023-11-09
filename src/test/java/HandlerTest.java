import com.greenapi.chatbot.pkg.BotHandler;
import com.greenapi.client.pkg.models.notifications.DeviceInfo;
import com.greenapi.client.pkg.models.notifications.StateInstanceChanged;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;

@TestComponent
public class HandlerTest extends BotHandler {

    @Override
    public void processStateInstanceChanged(StateInstanceChanged stateInstanceChanged) {
        super.processStateInstanceChanged(stateInstanceChanged);
    }

    @Override
    public void processDeviceInfo(DeviceInfo deviceInfo) {
        super.processDeviceInfo(deviceInfo);
    }
}
