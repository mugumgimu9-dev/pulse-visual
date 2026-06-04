package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class CriticalHitModule extends Module {
    private boolean enableParticles = true;
    private int particleColor = 0xFFFFFF00;
    private boolean enableSound = true;

    public CriticalHitModule() {
        super("CriticalHit", "Custom critical hit effects", CATEGORY_COMBAT);
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
        // Render critical hit effects
    }
}
