package com.greenapi.chatbot;

import com.greenapi.chatbot.pkg.BotFactory;
import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.chatbot.pkg.state.StateManagerHashMapImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Configuration
public class BotDefaultConfigExample {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StateManager stateManager() {
        return new StateManagerHashMapImpl(new HashMap<>());
    }

    @Bean
    @ConditionalOnMissingBean
    public BotFactory botFactory(RestTemplate restTemplate, StateManager stateManager) {
        return new BotFactory(restTemplate, stateManager);
    }
}
