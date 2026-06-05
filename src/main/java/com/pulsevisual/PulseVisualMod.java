package com.pulsevisual;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class PulseVisualMod implements ModInitializer {
    
    private static KeyBinding configKeyBinding;
    
    public static boolean enableEqualizer = true;
    public static boolean enableWaves = true;
    public static boolean chromaMode = true;

    @Override
    public void onInitialize() {
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

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                
                long time = System.currentTimeMillis();
                int centerX = client.getWindow().getScaledWidth() / 2;
                int centerY = client.getWindow().getScaledHeight() / 2;

                int chromaColor = java.awt.Color.HSBtoRGB((time % 4000) / 4000f, 0.8f, 1f);
                int mainColor = chromaMode ? chromaColor : 0xff00ffff;

                // Настройка OpenGL для гладкого рендеринга линий
                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.defaultBlendFunc();

                // 1. ОРИГИНАЛЬНЫЙ КРУГОВОЙ ЭКВАЛАЙЗЕР
                if (enableEqualizer) {
                    int barsCount = 60; // Больше полос для высокой детализации
                    double baseRadius = 35.0;

                    for (int i = 0; i < barsCount; i++) {
                        double angle = (i * (360.0 / barsCount)) * Math.PI / 180.0;
                        
                        // Создаем сложную волну спектра
                        double pulse = Math.sin((time / 150.0) + i * 0.4) * 6.0;
                        pulse += Math.cos((time / 250.0) - i * 0.2) * 4.0;
                        pulse = Math.max(0, pulse + 4.0); // Исключаем отрицательные значения

                        if (client.player.isSprinting()) {
                            pulse *= 1.4;
                        }

                        float r = ((mainColor >> 16) & 0xFF) / 255f;
                        float g = ((mainColor >> 8) & 0xFF) / 255f;
                        float b = (mainColor & 0xFF) / 255f;

                        double startX = centerX + Math.cos(angle) * baseRadius;
                        double startY = centerY + Math.sin(angle) * baseRadius;
                        double endX = centerX + Math.cos(angle) * (baseRadius + pulse);
                        double endY = centerY + Math.sin(angle) * (baseRadius + pulse);

                        // Отрезок рендерится как гладкая линия
                        drawLine(matrixStack, startX, startY, endX, endY, r, g, b, 1.0f, 2.0f);
                    }
                }

                // 2. ИДЕАЛЬНО КРУГЛЫЕ ВОЛНЫ
                if (enableWaves) {
                    for (int w = 0; w < 3; w++) {
                        long waveTime = time + (w * 700);
                        double radius = 15.0 + ((waveTime % 2100) / 25.0);
                        
                        float alpha = 1.0f - (float)((waveTime % 2100) / 2100.0);
                        
                        float r = ((mainColor >> 16) & 0xFF) / 255f;
                        float g = ((mainColor >> 8) & 0xFF) / 255f;
                        float b = (mainColor & 0xFF) / 255f;

                        drawSmoothCircle(matrixStack, centerX, centerY, radius, r, g, b, alpha);
                    }
                }

                RenderSystem.enableTexture();
                RenderSystem.disableBlend();

                // 3. ВОТЕРМАРК В СТИЛЕ PULSE VISUAL
                DrawableHelper.fill(matrixStack, 5, 5, 125, 23, 0xB0000000);
                DrawableHelper.fill(matrixStack, 5, 5, 7, 23, mainColor);
                client.textRenderer.draw(matrixStack, "§lPULSE§r VISUAL", 14, 9, mainColor);
            }
        });
    }

    // Метод отрисовки гладкой линии через OpenGL BufferBuilder
    private static void drawLine(MatrixStack matrices, double startX, double startY, double endX, double endY, float r, float g, float b, float a, float width) {
        RenderSystem.lineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)startX, (float)startY, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)endX, (float)endY, 0).color(r, g, b, a).next();
        
        tessellator.draw();
    }

    // Метод отрисовки идеальной окружности из 64 сегментов
    private static void drawSmoothCircle(MatrixStack matrices, double cx, double cy, double radius, float r, float g, float b, float a) {
        RenderSystem.lineWidth(1.5f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        int segments = 64;
        for (int i = 0; i <= segments; i++) {
            double angle = i * (Math.PI * 2 / segments);
            double x = cx + Math.cos(angle) * radius;
            double y = cy + Math.sin(angle) * radius;
            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)x, (float)y, 0).color(r, g, b, a).next();
        }

        tessellator.draw();
    }

    // Минималистичное ClickGUI меню
    public static class PulseVisualScreen extends Screen {
        public PulseVisualScreen() {
            super(new LiteralText("Pulse Visual"));
        }

        @Override
        protected void init() {
            int bWidth = 160;
            int bHeight = 20;
            int x = this.width / 2 - 80;
            int y = this.height / 2 - 30;

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

            this.addButton(new ButtonWidget(x, y + 85, bWidth, bHeight, new LiteralText("Close"), b -> this.client.openScreen(null)));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            int chromaColor = java.awt.Color.HSBtoRGB((System.currentTimeMillis() % 4000) / 4000f, 0.8f, 1f);
            drawCenteredText(matrices, this.textRenderer, "PULSE VISUAL CLIENT", this.width / 2, this.height / 2 - 50, chromaMode ? chromaColor : 0xff00ffff);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
