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
    
    // Оригинальные переключатели модулей Pulse Visual
    public static boolean targetHud = true;       // Круговой эквалайзер вокруг прицела
    public static boolean shockwaves = true;      // Круглые расходящиеся волны
    public static boolean chromaRGB = true;       // Фирменный перелив цветов

    @Override
    public void onInitialize() {
        // Регистрируем кнопку открытия меню на P
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

        // Движок отрисовки
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                
                long time = System.currentTimeMillis();
                int centerX = client.getWindow().getScaledWidth() / 2;
                int centerY = client.getWindow().getScaledHeight() / 2;

                // Генерация оригинального Chroma-эффекта
                int rgb = java.awt.Color.HSBtoRGB((time % 4000) / 4000f, 0.75f, 1f);
                float r = ((rgb >> 16) & 0xFF) / 255f;
                float g = ((rgb >> 8) & 0xFF) / 255f;
                float b = (rgb & 0xFF) / 255f;

                // Включаем блендинг OpenGL для мягких линий и прозрачности
                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.defaultBlendFunc();

                // 1. ОРИГИНАЛЬНЫЙ RADIAL EQUALIZER (Вокруг прицела)
                if (targetHud) {
                    int lines = 45; // Число лучей
                    double innerRadius = 28.0;

                    for (int i = 0; i < lines; i++) {
                        double angle = (i * (360.0 / lines)) * Math.PI / 180.0;
                        
                        // Просчет высоты луча (симуляция звуковой частоты клиента)
                        double dynamicPulse = Math.sin((time / 130.0) + i * 0.35) * 5.5;
                        dynamicPulse += Math.cos((time / 220.0) - i * 0.15) * 3.5;
                        dynamicPulse = Math.max(1.0, dynamicPulse + 3.0);

                        if (client.player.isSprinting()) {
                            dynamicPulse *= 1.3; // Усиление импульса при движении
                        }

                        double x1 = centerX + Math.cos(angle) * innerRadius;
                        double y1 = centerY + Math.sin(angle) * innerRadius;
                        double x2 = centerX + Math.cos(angle) * (innerRadius + dynamicPulse);
                        double y2 = centerY + Math.sin(angle) * (innerRadius + dynamicPulse);

                        drawPulseLine(matrixStack, x1, y1, x2, y2, r, g, b, 1.0f);
                    }
                }

                // 2. SHOCKWAVES (Идеально круглые волны с затуханием альфы)
                if (shockwaves) {
                    for (int step = 0; step < 3; step++) {
                        long waveOffset = time + (step * 650);
                        double radius = 12.0 + ((waveOffset % 1950) / 22.0);
                        float fadeAlpha = 1.0f - (float)((waveOffset % 1950) / 1950.0);

                        drawPulseCircle(matrixStack, centerX, centerY, radius, r, g, b, fadeAlpha);
                    }
                }

                RenderSystem.enableTexture();
                RenderSystem.disableBlend();

                // 3. СТИЛЬНЫЙ ВОТЕРМАРК PULSE
                DrawableHelper.fill(matrixStack, 6, 6, 120, 24, 0xAA000000);
                DrawableHelper.fill(matrixStack, 6, 6, 8, 24, rgb);
                client.textRenderer.draw(matrixStack, "PULSE CLIENT", 14, 10, rgb);
            }
        });
    }

    // Рендер гладкой неоновой линии
    private static void drawPulseLine(MatrixStack matrices, double x1, double y1, double x2, double y2, float r, float g, float b, float a) {
        RenderSystem.lineWidth(2.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrices.peek().getPositionMatrix(), (float)x1, (float)y1, 0).color(r, g, b, a).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float)x2, (float)y2, 0).color(r, g, b, a).next();
        tessellator.draw();
    }

    // Рендер точного круга волны (64 полигона)
    private static void drawPulseCircle(MatrixStack matrices, double cx, double cy, double radius, float r, float g, float b, float a) {
        RenderSystem.lineWidth(1.5f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        int points = 64;
        for (int i = 0; i <= points; i++) {
            double theta = i * (Math.PI * 2 / points);
            double x = cx + Math.cos(theta) * radius;
            double y = cy + Math.sin(theta) * radius;
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)x, (float)y, 0).color(r, g, b, a).next();
        }
        tessellator.draw();
    }

    // Меню настроек
    public static class PulseVisualScreen extends Screen {
        public PulseVisualScreen() {
            super(new LiteralText("Pulse Visual"));
        }

        @Override
        protected void init() {
            int w = 150;
            int h = 20;
            int x = this.width / 2 - 75;
            int y = this.height / 2 - 35;

            this.addButton(new ButtonWidget(x, y, w, h, new LiteralText("TargetHUD: " + (targetHUDName())), b -> {
                targetHud = !targetHud;
                b.setMessage(new LiteralText("TargetHUD: " + (targetHUDName())));
            }));

            this.addButton(new ButtonWidget(x, y + 24, w, h, new LiteralText("Shockwaves: " + (shockwaves ? "ON" : "OFF")), b -> {
                shockwaves = !shockwaves;
                b.setMessage(new LiteralText("Shockwaves: " + (shockwaves ? "ON" : "OFF")));
            }));

            this.addButton(new ButtonWidget(x, y + 48, w, h, new LiteralText("Chroma RGB: " + (chromaRGB ? "ON" : "OFF")), b -> {
                chromaRGB = !chromaRGB;
                b.setMessage(new LiteralText("Chroma RGB: " + (chromaRGB ? "ON" : "OFF")));
            }));

            this.addButton(new ButtonWidget(x, y + 78, w, h, new LiteralText("Close Menu"), b -> this.client.openScreen(null)));
        }

        private String targetHUDName() {
            return targetHud ? "ON" : "OFF";
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            int rgb = java.awt.Color.HSBtoRGB((System.currentTimeMillis() % 4000) / 4000f, 0.75f, 1f);
            drawCenteredText(matrices, this.textRenderer, "PULSE VISUAL CONTROL", this.width / 2, this.height / 2 - 55, chromaRGB ? rgb : 0xff00ffff);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
