package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class PulseVisual implements ModInitializer {
    @Override
    public void onInitialize() {
        // Регистрируем рендер текста прямо через Fabric API, в обход сложных миксинов!
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null) {
                // Выводим ярко-зелёную надпись в левом верхнем углу экрана
                client.textRenderer.draw(matrices, "§a[Pulse Visual Active v1.0]", 10, 10, 0xffffff);
            }
        });
    }
}
