package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class TracersModule extends Module {
    private boolean playerTracers = true;
    private boolean entityTracers = true;
    private int tracerColor = 0xFF00FF00;

    public TracersModule() {
        super("Tracers", "Draw lines to entities", CATEGORY_RENDER);
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
        // Draw tracer lines
    }
}
