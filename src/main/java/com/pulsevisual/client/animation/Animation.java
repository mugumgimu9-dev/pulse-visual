package com.pulsevisual.client.animation;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class Animation {
    private float value;
    private float target;
    private float speed;
    private long lastUpdateTime;

    public Animation(float initialValue, float speed) {
        this.value = initialValue;
        this.target = initialValue;
        this.speed = speed;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void animate(float target) {
        this.target = target;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        float delta = (float) deltaTime / 1000f;
        float change = speed * delta;

        if (value < target) {
            value = Math.min(value + change, target);
        } else if (value > target) {
            value = Math.max(value - change, target);
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isAnimating() {
        return value != target;
    }
}
