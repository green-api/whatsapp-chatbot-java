package com.greenapi.chatbot.pkg.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class State {
    private Map<String, Object> data;
}
