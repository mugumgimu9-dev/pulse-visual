package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding; // В 1.16.5 базовый KeyBinding лежит тут
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class PulseVisualMod implements ModInitializer {
    
    private static KeyBinding configKeyBinding;
    
    public static boolean enableEqualizer = false;
    public static boolean enableWaves = false;

    @Override
    public void onInitialize() {
        // Регистрируем кнопку меню на P
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.settings", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_P, 
                "category.pulsevisual.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.wasPressed()) {
                if (client.player != null) {
                    client.openScreen(new PulseVisualScreen());
                }
            }
        });

        // Рендер элементов интерфейса в игре
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && client.options != null && !client.options.hudHidden) {
                
                long time = System.currentTimeMillis();

                // 1. РЕНДЕР ЭКВАЛАЙЗЕРА (если включен)
                if (enableEqualizer) {
                    // Рисуем 5 прыгающих полосок внизу экрана
                    int startX = client.getWindow().getScaledWidth() / 2 - 40;
                    int startY = client.getWindow().getScaledHeight() - 60;
                    
                    for (int i = 0; i < 5; i++) {
                        // Вычисляем динамическую высоту полоски по синусоиде времени
                        int height = 10 + (int)(Math.sin((time / 150.0) + i) * 8 + 8);
                        // Рисуем неоново-голубую полоску эквалайзера
                        DrawableHelper.fill(matrixStack, startX + (i * 15), startY - height, startX + (i * 15) + 8, startY, 0xff00ffff);
                    }
                }

                // 2. РЕНДЕР КРУГОВЫХ ВОЛН (если включены)
                if (enableWaves) {
                    // Центр экрана
                    int centerX = client.getWindow().getScaledWidth() / 2;
                    int centerY = client.getWindow().getScaledHeight() / 2;
                    
                    // Вычисляем пульсирующий радиус круга (расширяется и сужается)
                    int waveRadius = 20 + (int)((time % 2000) / 50.0);
                    
                    // Рисуем тонкие полупрозрачные рамки в виде "волн" вокруг прицела
                    DrawableHelper.fill(matrixStack, centerX - waveRadius, centerY - waveRadius, centerX + waveRadius, centerY - waveRadius + 1, 0x80ffffff);
                    DrawableHelper.fill(matrixStack, centerX - waveRadius, centerY + waveRadius - 1, centerX + waveRadius, centerY + waveRadius, 0x80ffffff);
                    DrawableHelper.fill(matrixStack, centerX - waveRadius, centerY - waveRadius, centerX - waveRadius + 1, centerY + waveRadius, 0x80ffffff);
                    DrawableHelper.fill(matrixStack, centerX + waveRadius - 1, centerY - waveRadius, centerX + waveRadius, centerY + waveRadius, 0x80ffffff);
                }

                // 3. Главная плашка мода в углу
                DrawableHelper.fill(matrixStack, 5, 5, 155, 45, 0x80000000);
                client.textRenderer.draw(matrixStack, "§bPulse Visual §a[Active]", 10, 10, 0xffffff);
                
                String eqStatus = enableEqualizer ? "§aON" : "§cOFF";
                String waveStatus = enableWaves ? "§aON" : "§cOFF";
                
                client.textRenderer.draw(matrixStack, "Equalizer: " + eqStatus, 10, 22, 0xffffff);
                client.textRenderer.draw(matrixStack, "Wave Circles: " + waveStatus, 10, 32, 0xffffff);
            }
        });
    }

    // Экран настроек GUI
    public static class PulseVisualScreen extends Screen {
        public PulseVisualScreen() {
            super(new LiteralText("Pulse Visual Settings"));
        }

        @Override
        protected void init() {
            int buttonWidth = 200;
            int buttonHeight = 20;
            int centerX = this.width / 2 - 100;
            int centerY = this.height / 2;

            // Кнопка Эквалайзера
            this.addButton(new ButtonWidget(centerX, centerY - 40, buttonWidth, buttonHeight, 
                new LiteralText("Equalizer: " + (enableEqualizer ? "ON" : "OFF")), button -> {
                    enableEqualizer = !enableEqualizer;
                    button.setMessage(new LiteralText("Equalizer: " + (enableEqualizer ? "ON" : "OFF")));
            }));

            // Кнопка Волн
            this.addButton(new ButtonWidget(centerX, centerY - 15, buttonWidth, buttonHeight, 
                new LiteralText("Wave Circles: " + (enableWaves ? "ON" : "OFF")), button -> {
                    enableWaves = !enableWaves;
                    button.setMessage(new LiteralText("Wave Circles: " + (enableWaves ? "ON" : "OFF")));
            }));

            // Кнопка закрытия
            this.addButton(new ButtonWidget(centerX, centerY + 20, buttonWidth, buttonHeight, 
                new LiteralText("Close Menu"), button -> {
                    this.client.openScreen(null);
            }));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            drawCenteredText(matrices, this.textRenderer, "§bPulse Visual §fOptions", this.width / 2, this.height / 2 - 70, 0xffffff);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
