package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class HUDModule extends Module {
    private boolean showFPS = true;
    private boolean showCoordinates = true;
    private boolean showDirection = true;
    private boolean animateElements = true;
    private int hudOpacity = 255;

    public HUDModule() {
        super("HUD", "Main HUD display with animations", CATEGORY_RENDER);
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
        // Render main HUD
    }
}
