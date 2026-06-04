package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class KillEffectsModule extends Module {
    private boolean enableSounds = true;
    private boolean enableParticles = true;
    private String effectType = "explosion";

    public KillEffectsModule() {
        super("KillEffects", "Visual and sound effects on kill", CATEGORY_COMBAT);
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
        // Render kill effects
    }
}
