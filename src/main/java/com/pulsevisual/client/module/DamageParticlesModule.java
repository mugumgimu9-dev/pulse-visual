package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class DamageParticlesModule extends Module {
    private int particleColor = 0xFFFF0000;
    private boolean enableAnimations = true;
    private float particleSize = 1.0f;

    public DamageParticlesModule() {
        super("DamageParticles", "Custom damage particle effects", CATEGORY_COMBAT);
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
        // Render damage particles
    }
}
