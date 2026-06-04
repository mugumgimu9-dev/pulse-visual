package com.pulsevisual.client.manager;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class KeybindManager {
    private Map<String, Integer> keybinds = new HashMap<>();

    public void registerKeybinds() {
        // Register default keybinds
        keybinds.put("gui", 80); // P key
        keybinds.put("fullbright", 70); // F key
    }

    public void registerKeybind(String name, int keyCode) {
        keybinds.put(name, keyCode);
    }

    public int getKeybind(String name) {
        return keybinds.getOrDefault(name, -1);
    }

    public void saveKeybinds() {
        // Save keybinds to config
    }

    public void loadKeybinds() {
        // Load keybinds from config
    }
}
