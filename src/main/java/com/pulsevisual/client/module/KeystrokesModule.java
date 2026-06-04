package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class KeystrokesModule extends Module {
    private boolean showMouse = true;
    private boolean showKeyboard = true;
    private int scale = 100;
    private int opacity = 255;

    public KeystrokesModule() {
        super("Keystrokes", "Display keyboard and mouse input", CATEGORY_UTILITY);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {
        // Render keystrokes display
    }
}
