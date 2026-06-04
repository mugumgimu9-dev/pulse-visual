package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class FullbrightModule extends Module {
    private float originalGamma;

    public FullbrightModule() {
        super("Fullbright", "Brightens the world", CATEGORY_RENDER);
    }

    @Override
    public void onEnable() {
        // Store original gamma and set to max
    }

    @Override
    public void onDisable() {
        // Restore original gamma
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender() {
    }
}
