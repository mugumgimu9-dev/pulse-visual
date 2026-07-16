package com.pulsevisual.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.pulsevisual.client.manager.ModuleManager;
import com.pulsevisual.client.manager.ConfigManager;
import com.pulsevisual.client.manager.KeybindManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.features.FastExpThrower;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Environment(EnvType.CLIENT)
public class PulseVisualClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("PulseVisualClient");

    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static KeybindManager keybindManager;

    private final TabletTriggerBot triggerBot = new TabletTriggerBot();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            triggerBot.onTick(client);
        });
    }

    public static class TabletTriggerBot {
        private final boolean isEnabled = true;
        private final boolean attackPlayersOnly = true;

        public void onTick(MinecraftClient client) {
            if (!isEnabled || client.player == null || client.world == null || client.interactionManager == null) return;

            ClientPlayerEntity player = client.player;

            if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

            HitResult hitResult = client.crosshairTarget;

            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                Entity target = entityHitResult.getEntity();

                // Исправлено: проверяем только что цель жива
                if (target != null && target.isAlive()) {
                    if (attackPlayersOnly && !(target instanceof PlayerEntity)) {
                        return;
                    }

                    if (target == player) return;

                    client.interactionManager.attackEntity(player, target);
                    player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}
