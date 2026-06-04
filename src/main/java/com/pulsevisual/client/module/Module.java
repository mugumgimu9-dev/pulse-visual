package com.pulsevisual.client.module;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public abstract class Module {
    protected String name;
    protected String description;
    protected boolean enabled;
    protected int keyCode;
    protected int category;
    
    public static final int CATEGORY_RENDER = 0;
    public static final int CATEGORY_COMBAT = 1;
    public static final int CATEGORY_MOVEMENT = 2;
    public static final int CATEGORY_PLAYER = 3;
    public static final int CATEGORY_UTILITY = 4;

    public Module(String name, String description, int category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keyCode = -1;
    }

    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onUpdate();
    public abstract void onRender();

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public int getKeyCode() { return keyCode; }
    public int getCategory() { return category; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
}
