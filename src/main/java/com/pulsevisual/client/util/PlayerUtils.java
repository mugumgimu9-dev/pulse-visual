package com.pulsevisual.client.util;

import net.minecraft.client.MinecraftClient;

public class PlayerUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static float getYaw() {
        if (client.player == null) return 0.0F;
        return client.player.getYaw(1.0F);
    }

    public static float getPitch() {
        if (client.player == null) return 0.0F;
        return client.player.getPitch(1.0F);
    }
}
