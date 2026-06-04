package com.pulsevisual.client.util;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EntityUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static double getDistance(Entity entity) {
        if (client.player == null) return 0;
        return client.player.distanceTo(entity);
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof net.minecraft.entity.player.PlayerEntity;
    }

    public static boolean isMonster(Entity entity) {
        return entity instanceof net.minecraft.entity.mob.HostileEntity;
    }

    public static boolean isAnimal(Entity entity) {
        return entity instanceof net.minecraft.entity.passive.AnimalEntity;
    }

    public static Vec3d getEntityPosition(Entity entity) {
        return entity.getPos();
    }

    public static float getEntityYaw(Entity entity) {
        return entity.getYaw();
    }

    public static float getEntityPitch(Entity entity) {
        return entity.getPitch();
    }
}
