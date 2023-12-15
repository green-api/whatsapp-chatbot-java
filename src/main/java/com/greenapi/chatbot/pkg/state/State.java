package com.greenapi.chatbot.pkg.state;

import com.greenapi.chatbot.pkg.Scene;

import java.util.Map;

public interface State {
    Map<String, Object> getData();
    void setData(Map<String, Object> data);
    Scene getScene();
    void setScene(Scene scene);
}
