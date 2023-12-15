package com.greenapi.chatbot.pkg.state;

import java.util.Map;
import java.util.Optional;

public interface StateManager {

    Optional<State> get(String stateId);

    MapState create(String stateId);

    void update(String stateId);

    void delete(String stateId);

    Optional<Map<String, Object>> getStateData(String stateId);

    void setStateData(String stateId, Map<String, Object> stateData);

    void updateStateData(String stateId, Map<String, Object> stateData);

    void deleteStateData(String stateId);
}