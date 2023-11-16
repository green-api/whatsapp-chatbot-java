package com.greenapi.chatbot.pkg.state;

import com.greenapi.chatbot.pkg.exception.BotStateException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StateManagerHashMapImpl implements StateManager {
    private final Map<String, State> states;
    private final Map<String, Object> initStateData;

    public StateManagerHashMapImpl() {
        this.states = new HashMap<>();
        this.initStateData = new HashMap<>();
    }

    public StateManagerHashMapImpl(Map<String, Object> initStateData) {
        this.states = new HashMap<>();
        this.initStateData = initStateData;
    }

    @Override
    public Optional<State> get(String chatId) {
        return Optional.ofNullable(states.get(chatId));
    }

    @Override
    public State create(String chatId) {
        states.put(chatId, new State(new HashMap<>(initStateData)));
        return get(chatId).orElseThrow(BotStateException::new);
    }

    @Override
    public void update(String chatId) {}

    @Override
    public void delete(String chatId) {
        states.remove(chatId);
    }

    @Override
    public Optional<Map<String, Object>> getStateData(String chatId) {
        var state = states.get(chatId);
        return state != null ? Optional.ofNullable(state.getData()) : Optional.empty();
    }

    @Override
    public void setStateData(String chatId, Map<String, Object> stateData) {
        var state = states.get(chatId);
        if (state != null) {
            state.setData(stateData);
        }
    }

    @Override
    public void updateStateData(String chatId, Map<String, Object> newStateData) {
        var state = states.get(chatId);
        if (state != null) {
            if (state.getData() == null) {
                state.setData(newStateData);
            } else {
                state.getData().putAll(newStateData);
            }
        }
    }

    @Override
    public void deleteStateData(String chatId) {
        State state = states.get(chatId);
        if (state != null) {
            state.setData(new HashMap<>(initStateData));
        }
    }
}
