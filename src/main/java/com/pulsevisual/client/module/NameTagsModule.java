package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class NameTagsModule extends Module {
    private boolean showDistance = true;
    private boolean showHealth = true;
    private int scale = 100;

    public NameTagsModule() {
        super("NameTags", "Customize entity nametags", CATEGORY_RENDER);
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
        // Render custom nametags
    }
}
