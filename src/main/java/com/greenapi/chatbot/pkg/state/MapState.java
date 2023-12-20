package com.greenapi.chatbot.pkg.state;

import com.greenapi.chatbot.pkg.Scene;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class MapState implements State {
    private Map<String, Object> data;

    @Override
    public Scene getScene() {
        var scene = data.get("scene");
        if (scene != null) {
            return (Scene) scene;
        }
        return null;
    }

    @Override
    public void setScene(Scene scene) {
        data.put("scene", scene);
    }
}
