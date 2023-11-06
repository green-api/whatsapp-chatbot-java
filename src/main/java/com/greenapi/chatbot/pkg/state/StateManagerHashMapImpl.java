package com.greenapi.chatbot.pkg.state;

import com.greenapi.chatbot.pkg.exception.BotStateException;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class StateManagerHashMapImpl implements StateManager {
    private Map<String, State> storage;

    @Override
    public Optional<State> get(String sender) {
        return Optional.ofNullable(storage.get(sender));
    }

    @Override
    public State create(String sender) {
        storage.put(sender, new State(new HashMap<>()));
        return get(sender).orElseThrow(BotStateException::new);
    }

    @Override
    public void delete(String sender) {
        storage.remove(sender);
    }

    @Override
    public Optional<Map<String, Object>> getStateData(String sender) {
        var state = storage.get(sender);
        return state != null ? Optional.ofNullable(state.getData()) : Optional.empty();
    }

    @Override
    public void setStateData(String sender, Map<String, Object> stateData) {
        var state = storage.get(sender);
        if (state != null) {
            state.setData(stateData);
        }
    }

    @Override
    public void updateStateData(String sender, Map<String, Object> stateData) {
        var state = storage.get(sender);
        if (state != null) {
            if (state.getData() == null) {
                state.setData(stateData);
            } else {
                state.getData().putAll(stateData);
            }
        }
    }

    @Override
    public void deleteStateData(String sender) {
        State state = storage.get(sender);
        if (state != null) {
            state.setData(null);
        }
    }

    @Override
    public State getOrCreate(String stateId) {

        return get(stateId).orElse(create(stateId));
    }
}
