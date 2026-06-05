package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class PulseVisualMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Регистрируем рендер интерфейса через Fabric API события
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Проверяем, что игрок зашел в мир и шрифты игры загрузились
            if (client.player != null && client.textRenderer != null) {
                // Рисуем зелёную надпись в левом верхнем углу экрана (координаты X=10, Y=10)
                client.textRenderer.draw(matrixStack, "§a[Pulse Visual Active v1.0]", 10, 10, 0xffffff);
            }
        });
    }
}
