package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class PotionStatusModule extends Module {
    private boolean showDuration = true;
    private int scale = 100;
    private int posX = 0;
    private int posY = 0;

    public PotionStatusModule() {
        super("PotionStatus", "Display active potion effects", CATEGORY_UTILITY);
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
        // Render potion status
    }
}
