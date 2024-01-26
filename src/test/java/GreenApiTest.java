import com.greenapi.chatbot.examples.DefaultHandler;
import com.greenapi.chatbot.examples.echo.EchoStartScene;
import com.greenapi.chatbot.pkg.BotFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import java.util.concurrent.CompletableFuture;

@SpringBootTest(classes = {BotTestConfig.class, DefaultHandler.class})
@RequiredArgsConstructor
@TestComponent
public class GreenApiTest {
    @Test
    public void initBot(@Value("${green-api.instanceId}") String instanceId,
                        @Value("${green-api.token}") String instanceToken,
                        @Autowired BotFactory botFactory) throws InterruptedException {

        var bot = botFactory.createBot(
            instanceId,
            instanceToken);

        bot.setStartScene(new EchoStartScene());

        CompletableFuture.runAsync(bot::startReceivingNotifications);

        Thread.sleep(10000);
        bot.stopReceivingNotifications();
    }
}
