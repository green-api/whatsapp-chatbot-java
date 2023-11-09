package com.greenapi.chatbot.pkg.state;

import java.util.Map;
import java.util.Optional;

public interface StateManager {

    Optional<State> get(String sender);

    State create(String sender);

    void update(String sender);

    void delete(String sender);

    Optional<Map<String, Object>> getStateData(String sender);

    void setStateData(String sender, Map<String, Object> stateData);

    void updateStateData(String sender, Map<String, Object> stateData);

    void deleteStateData(String sender);
}