package com.pulsevisual.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

public class AdvancedESP {

    private final MinecraftClient client = MinecraftClient.getInstance();

    // --- НАСТРОЙКИ МОДУЛЯ (Пока жестко прописаны, т.к. нет GUI) ---
    public boolean isEnabled = true;

    // Режим подсветки: 0 = Отключен, 1 = Боксы (3D), 2 = Щейдеры (Аутлайн)
    public int renderMode = 1;

    // Режим выбора цвета: 0 = Статичный, 1 = По здоровью (Радуга), 2 = Радуга
    public int colorMode = 0;

    // Статичный цвет (если colorMode = 0)
    public Color staticColor = Color.RED;

    // Прозрачность (0-255)
    public int espAlpha = 150;

    // --- ИНИЦИАЛИЗАЦИЯ ---
    public void init() {
        // Регистрируем рендер ивент
        WorldRenderEvents.LAST.register(this::onRenderWorld);
    }

    private void onRenderWorld(WorldRenderContext context) {
        if (!isEnabled || client.world == null || client.player == null) return;

        List<? extends PlayerEntity> players = client.world.getPlayers();
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();

        for (PlayerEntity player : players) {
            if (player == client.player) continue; // Не подсвечиваем себя

            // Проверяем дистанцию (например, 128 блоков)
            if (client.player.distanceTo(player) > 128) continue;

            Color drawColor = getESPColor(player);

            // Обработка режимов рендера
            switch (renderMode) {
                case 1 -> draw3DBoxESP(matrices, camera, player, drawColor);
                case 2 -> applyShaderOutline(player, drawColor);
            }
        }
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ РЕНДЕРА ---

    // 1. Метод для 3D Боксов
    private void draw3DBoxESP(MatrixStack matrices, Camera camera, Entity entity, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        Vec3d cameraPos = camera.getPos();
        double x = entity.getX() - cameraPos.x;
        double y = entity.getY() - cameraPos.y;
        double z = entity.getZ() - cameraPos.z;

        Box box = entity.getBoundingBox();
        Box drawBox = new Box(x - (box.getXLength() / 2), y, z - (box.getZLength() / 2),
                               x + (box.getXLength() / 2), y + box.getYLength(), z + (box.getZLength() / 2));

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Рисуем грани бокса (с прозрачностью)
        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = espAlpha;
        drawBox(bufferBuilder, drawBox, r, g, b, a);

        tessellator.draw();

        // Рисуем контур бокса
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        WorldRenderer.drawBox(matrices, bufferBuilder, drawBox, r/255f, g/255f, b/255f, a/255f);
        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    // Вспомогательный метод для рисования заполненного бокса
    private void drawBox(BufferBuilder bufferBuilder, Box box, int r, int g, int b, int a) {
        float f1 = (float)box.minX, f2 = (float)box.minY, f3 = (float)box.minZ;
        float f4 = (float)box.maxX, f5 = (float)box.maxY, f6 = (float)box.maxZ;

        // Down
        bufferBuilder.vertex(f1, f2, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f2, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f2, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f2, f6).color(r, g, b, a).next();

        // Up
        bufferBuilder.vertex(f1, f5, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f5, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f5, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f5, f3).color(r, g, b, a).next();

        // North
        bufferBuilder.vertex(f1, f2, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f5, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f5, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f2, f3).color(r, g, b, a).next();

        // South
        bufferBuilder.vertex(f1, f2, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f2, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f5, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f5, f6).color(r, g, b, a).next();

        // West
        bufferBuilder.vertex(f1, f2, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f2, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f5, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f1, f5, f3).color(r, g, b, a).next();

        // East
        bufferBuilder.vertex(f4, f2, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f5, f3).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f5, f6).color(r, g, b, a).next();
        bufferBuilder.vertex(f4, f2, f6).color(r, g, b, a).next();
    }

    // 2. Метод для Шейдеров (Аутлайна)
    // ВАЖНО: Ванильный Fabric аутлайн работает нестабильно без Mixin'ов.
    // Это базовая реализация черезGlowing эффект.
    private void applyShaderOutline(PlayerEntity player, Color color) {
        // Мы просто включаем свечение ванильным способом
        // Чтобы менять ЦВЕТ свечения, нужны продвинутые миксины в RenderLayers.
        // Сейчас он будет просто ванильно-белым/командным.
        player.setGlowing(true);
    }
    // Метод для выключения аутлайна (нужно вызывать, когда модуль выключается)
    public void disableShaders() {
        if (client.world != null) {
            client.world.getPlayers().forEach(p -> p.setGlowing(false));
        }
    }

    // --- ЛОГИКА ЦВЕТОВ ---
    private Color getESPColor(PlayerEntity player) {
        switch (colorMode) {
            case 1: // По здоровью (От зеленого к красному)
                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float hue = Math.max(0.0F, Math.min(health / maxHealth, 1.0F)) / 3.0F; // 0.0-0.33 (красный-зеленый)
                return Color.getHSBColor(hue, 1.0F, 1.0F);
            case 2: // Радуга
                float rainbowHue = (System.currentTimeMillis() % 3000) / 3000f;
                return Color.getHSBColor(rainbowHue, 1.0F, 1.0F);
            default: // Статичный
                return staticColor;
        }
    }
                              }
