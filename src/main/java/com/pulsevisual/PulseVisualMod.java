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
    
    // Настройки мода
    public static boolean enableEqualizer = true;
    public static boolean enableWaves = true;
    public static boolean chromaMode = true; // Плавная смена цвета (Радуга)

    @Override
    public void onInitialize() {
        // Кнопка открытия GUI (на P)
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

        // Продвинутый HUD рендер
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                
                long time = System.currentTimeMillis();
                int centerX = client.getWindow().getScaledWidth() / 2;
                int centerY = client.getWindow().getScaledHeight() / 2;

                // Вычисляем динамический цвет (Chroma / Радуга)
                int chromaColor = java.awt.Color.HSBtoRGB((time % 5000) / 5000f, 0.8f, 1f);
                int mainColor = chromaMode ? chromaColor : 0xff00ffff; // Радуга или Голубой

                // 1. КРУГОВОЙ ЭКВАЛАЙЗЕР (Вокруг прицела)
                if (enableEqualizer) {
                    int barsCount = 36; // Количество палочек в круге
                    double baseRadius = 30.0; // Радиус круга вокруг прицела

                    for (int i = 0; i < barsCount; i++) {
                        // Вычисляем угол для каждой палочки
                        double angle = (i * (360.0 / barsCount)) * Math.PI / 180.0;
                        
                        // Динамическая высота палочки, зависящая от времени и индекса
                        double pulse = Math.sin((time / 200.0) + i * 0.5) * 7.0 + 7.0;
                        // Если игрок бежит — палочки прыгают сильнее!
                        if (client.player.isSprinting()) pulse *= 1.5; 

                        double startX = centerX + Math.cos(angle) * baseRadius;
                        double startY = centerY + Math.sin(angle) * baseRadius;
                        double endX = centerX + Math.cos(angle) * (baseRadius + pulse);
                        double endY = centerY + Math.sin(angle) * (baseRadius + pulse);

                        // Рисуем палочки эквалайзера в виде пиксельных линий
                        DrawableHelper.fill(matrixStack, (int)startX, (int)startY, (int)endX + 1, (int)endY + 1, mainColor);
                    }
                }

                // 2. ПОЛУПРОЗРАЧНЫЕ РАСШИРЯЮЩИЕСЯ ВОЛНЫ
                if (enableWaves) {
                    // Создаем эффект трех бегущих кругов-волн
                    for (int w = 0; w < 3; w++) {
                        long waveTime = time + (w * 600);
                        int radius = 15 + (int)((waveTime % 1800) / 30.0);
                        
                        // Плавное исчезновение волны к краям (альфа-канал)
                        int alpha = 255 - (int)((waveTime % 1800) / 1800.0 * 255.0);
                        int waveColor = (alpha << 24) | (mainColor & 0x00ffffff);

                        // Отрисовка тонкого контура круга вокруг центра
                        drawTargetCircle(matrixStack, centerX, centerY, radius, waveColor);
                    }
                }

                // 3. Стильный вотермарк в углу экрана (стиль Pulse Visual)
                DrawableHelper.fill(matrixStack, 4, 4, 130, 22, 0x90000000); // Фон
                DrawableHelper.fill(matrixStack, 4, 4, 6, 22, mainColor); // Полоска слева
                client.textRenderer.draw(matrixStack, "§lPULSE§r VISUAL", 12, 8, mainColor);
            }
        });
    }

    // Вспомогательный метод для рисования аккуратных контуров волн
    private static void drawTargetCircle(MatrixStack matrices, int cx, int cy, int r, int color) {
        DrawableHelper.fill(matrices, cx - r, cy - r, cx + r, cx - r + 1, color);
        DrawableHelper.fill(matrices, cx - r, cy + r - 1, cx + r, cy + r, color);
        DrawableHelper.fill(matrices, cx - r, cy - r, cx - r + 1, cy + r, color);
        DrawableHelper.fill(matrices, cx + r - 1, cy - r, cx + r, cy + r, color);
    }

    // Прокачанное GUI меню
    public static class PulseVisualScreen extends Screen {
        public PulseVisualScreen() {
            super(new LiteralText("Pulse Visual"));
        }

        @Override
        protected void init() {
            int bWidth = 160;
            int bHeight = 20;
            int x = this.width / 2 - 80;
            int y = this.height / 2 - 40;

            this.addButton(new ButtonWidget(x, y, bWidth, bHeight, new LiteralText("Equalizer: " + (enableEqualizer ? "ON" : "OFF")), b -> {
                enableEqualizer = !enableEqualizer;
                b.setMessage(new LiteralText("Equalizer: " + (enableEqualizer ? "ON" : "OFF")));
            }));

            this.addButton(new ButtonWidget(x, y + 25, bWidth, bHeight, new LiteralText("Wave Circles: " + (enableWaves ? "ON" : "OFF")), b -> {
                enableWaves = !enableWaves;
                b.setMessage(new LiteralText("Wave Circles: " + (enableWaves ? "ON" : "OFF")));
            }));

            this.addButton(new ButtonWidget(x, y + 50, bWidth, bHeight, new LiteralText("Chroma RGB: " + (chromaMode ? "ON" : "OFF")), b -> {
                chromaMode = !chromaMode;
                b.setMessage(new LiteralText("Chroma RGB: " + (chromaMode ? "ON" : "OFF")));
            }));

            this.addButton(new ButtonWidget(x, y + 85, bWidth, bHeight, new LiteralText("Back to Game"), b -> this.client.openScreen(null)));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            int chromaColor = java.awt.Color.HSBtoRGB((System.currentTimeMillis() % 5000) / 5000f, 0.8f, 1f);
            drawCenteredText(matrices, this.textRenderer, "PULSE VISUAL CLIENT", this.width / 2, this.height / 2 - 60, chromaMode ? chromaColor : 0xff00ffff);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
