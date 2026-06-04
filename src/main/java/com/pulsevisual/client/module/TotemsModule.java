package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class TotemsModule extends Module {
    private boolean showPopParticles = true;
    private boolean playSounds = true;
    private int particleColor = 0xFFFF6B6B;

    public TotemsModule() {
        super("Totems", "Totem pop effects and sounds", CATEGORY_COMBAT);
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
        // Render totem pop effects
    }
}
