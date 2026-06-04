package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class ESPModule extends Module {
    private boolean playersESP = true;
    private boolean monstersESP = true;
    private boolean animalsESP = false;
    private boolean itemsESP = false;

    public ESPModule() {
        super("ESP", "Highlight entities with boxes", CATEGORY_RENDER);
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
        // Render ESP boxes
    }
}
