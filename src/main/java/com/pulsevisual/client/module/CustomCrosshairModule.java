package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class CustomCrosshairModule extends Module {
    private int crosshairType = 0; // 0 = default, 1 = dot, 2 = custom
    private int crosshairColor = 0xFFFFFFFF;
    private int crosshairSize = 10;

    public CustomCrosshairModule() {
        super("CustomCrosshair", "Customize your crosshair", CATEGORY_RENDER);
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
        // Render custom crosshair
    }
}
