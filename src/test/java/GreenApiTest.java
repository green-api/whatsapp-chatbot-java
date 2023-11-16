import com.greenapi.chatbot.examples.HandlerExample;
import com.greenapi.chatbot.examples.full.FullStartScene;
import com.greenapi.chatbot.pkg.BotFactory;
import com.greenapi.chatbot.pkg.BotHandler;
import com.greenapi.client.pkg.api.GreenApi;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = {BotTestConfig.class, HandlerTest.class})
@RequiredArgsConstructor
@TestComponent
public class GreenApiTest {
    @Test
    public void initBot(@Value("${green-api.instanceId}") String instanceId,
                        @Value("${green-api.token}") String instanceToken,
                        @Autowired BotFactory botFactory,
                        @Autowired BotHandler botHandler) throws ExecutionException, InterruptedException {

        var bot = botFactory.createBot(
            instanceId,
            instanceToken,
            botHandler,
            new FullStartScene());

        bot.startReceivingNotifications();

        CompletableFuture.runAsync(bot::startReceivingNotifications);

        Thread.sleep(10000);
        bot.stopReceivingNotifications();
    }
}
