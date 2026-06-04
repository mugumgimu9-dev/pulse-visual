package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class ArmorStatusModule extends Module {
    private boolean showDamage = true;
    private int scale = 100;
    private int posX = 0;
    private int posY = 0;

    public ArmorStatusModule() {
        super("ArmorStatus", "Display armor durability", CATEGORY_UTILITY);
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
        // Render armor status
    }
}
