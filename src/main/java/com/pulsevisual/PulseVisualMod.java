package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
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
        // Регистрируем кнопку меню на P (в категории "Pulse Visual")
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.settings", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_P, 
                "category.pulsevisual.general"
        ));

        // Проверяем нажатие клавиши P каждый тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.wasPressed()) {
                if (client.player != null) {
                    client.openScreen(new PulseVisualScreen());
                }
            }
        });

        // Отрисовка интерфейса мода в игре
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && client.options != null && !client.options.hudHidden) {
                
                long time = System.currentTimeMillis();

                // 1. Прыгающий эквалайзер
                if (enableEqualizer) {
                    int startX = client.getWindow().getScaledWidth() / 2 - 40;
                    int startY = client.getWindow().getScaledHeight() - 60;
                    
                    for (int i = 0; i < 5; i++) {
                        int height = 10 + (int)(Math.sin((time / 150.0) + i) * 8 + 8);
                        DrawableHelper.fill(matrixStack, startX + (i * 15), startY - height, startX + (i * 15) + 8, startY, 0xff00ffff);
                    }
                }

                // 2. Расширяющиеся круговые волны у прицела
                if (enableWaves) {
                    int centerX = client.getWindow().getScaledWidth() / 2;
                    int centerY = client.getWindow().getScaledHeight() / 2;
                    int waveRadius = 20 + (int)((time % 2000) / 50.0);
                    
                    DrawableHelper.fill(matrixStack, centerX - waveRadius, centerY - waveRadius, centerX + waveRadius, centerY - waveRadius + 1, 0x80ffffff);
                    DrawableHelper.fill(matrixStack, centerX - waveRadius, centerY + waveRadius - 1, centerX + waveRadius, centerY + waveRadius, 0x80ffffff);
                    DrawableHelper.fill(matrixStack, centerX - waveRadius, centerY - waveRadius, centerX - waveRadius + 1, centerY + waveRadius, 0x80ffffff);
                    DrawableHelper.fill(matrixStack, centerX + waveRadius - 1, centerY - waveRadius, centerX + waveRadius, centerY + waveRadius, 0x80ffffff);
                }

                // 3. Информационная панель в левом углу экрана
                DrawableHelper.fill(matrixStack, 5, 5, 155, 45, 0x80000000);
                client.textRenderer.draw(matrixStack, "§bPulse Visual §a[Active]", 10, 10, 0xffffff);
                
                String eqStatus = enableEqualizer ? "§aON" : "§cOFF";
                String waveStatus = enableWaves ? "§aON" : "§cOFF";
                
                client.textRenderer.draw(matrixStack, "Equalizer: " + eqStatus, 10, 22, 0xffffff);
                client.textRenderer.draw(matrixStack, "Wave Circles: " + waveStatus, 10, 32, 0xffffff);
            }
        });
    }

    // Внутренний класс экрана настроек
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

            // Кнопка включения/выключения Эквалайзера
            this.addButton(new ButtonWidget(centerX, centerY - 40, buttonWidth, buttonHeight, 
                new LiteralText("Equalizer: " + (enableEqualizer ? "ON" : "OFF")), button -> {
                    enableEqualizer = !enableEqualizer;
                    button.setMessage(new LiteralText("Equalizer: " + (enableEqualizer ? "ON" : "OFF")));
            }));

            // Кнопка включения/выключения Волн
            this.addButton(new ButtonWidget(centerX, centerY - 15, buttonWidth, buttonHeight, 
                new LiteralText("Wave Circles: " + (enableWaves ? "ON" : "OFF")), button -> {
                    enableWaves = !enableWaves;
                    button.setMessage(new LiteralText("Wave Circles: " + (enableWaves ? "ON" : "OFF")));
            }));

            // Кнопка возврата в игру
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
