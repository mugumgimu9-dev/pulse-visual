package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;

public class PulseVisualMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Регистрируем рендер элементов интерфейса
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Проверяем, что игрок в мире, открыт интерфейс и шрифты загружены
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                
                // 1. Рисуем небольшую полупрозрачную подложку (черный прямоугольник)
                // Параметры: matrixStack, x1, y1, x2, y2, цвет в формате ARGB
                DrawableHelper.fill(matrixStack, 5, 5, 145, 25, 0x80000000);
                
                // 2. Поверх подложки выводим красивый текст
                client.textRenderer.draw(matrixStack, "§bPulse Visual §a[Active]", 10, 10, 0xffffff);
            }
        });
    }
}
