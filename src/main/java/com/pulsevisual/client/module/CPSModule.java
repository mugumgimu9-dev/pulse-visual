package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class CPSModule extends Module {
    private int cpsCount = 0;
    private long lastClickTime = 0;
    private int scale = 100;

    public CPSModule() {
        super("CPS", "Clicks per second counter", CATEGORY_UTILITY);
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
        // Render CPS counter
    }
}
