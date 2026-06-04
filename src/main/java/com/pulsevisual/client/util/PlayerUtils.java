package com.pulsevisual.client.util;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public class PlayerUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static Vec3d getPlayerPos() {
        if (client.player == null) return Vec3d.ZERO;
        return client.player.getPos();
    }

    public static float getPlayerYaw() {
        if (client.player == null) return 0;
        return client.player.getYaw();
    }

    public static float getPlayerPitch() {
        if (client.player == null) return 0;
        return client.player.getPitch();
    }

    public static int getPlayerHealth() {
        if (client.player == null) return 0;
        return (int) client.player.getHealth();
    }

    public static int getPlayerHunger() {
        if (client.player == null) return 0;
        return client.player.getHungerManager().getFoodLevel();
    }

    public static boolean isPlayerMoving() {
        if (client.player == null) return false;
        return client.player.getVelocity().length() > 0;
    }
}
