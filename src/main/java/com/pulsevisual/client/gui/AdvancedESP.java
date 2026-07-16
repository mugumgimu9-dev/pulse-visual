package com.pulsevisual.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

public class AdvancedESP {

    private final MinecraftClient client = MinecraftClient.getInstance();

    public boolean isEnabled = true;
    public int renderMode = 1; // 1 = Боксы, 2 = Шейдеры
    public int colorMode = 0;  // 0 = Статичный, 1 = По здоровью, 2 = Радуга
    public Color staticColor = Color.RED;

    public void init() {
        WorldRenderEvents.LAST.register(this::onRenderWorld);
    }

    private void onRenderWorld(WorldRenderContext context) {
        if (!isEnabled || client.world == null || client.player == null) return;

        List<? extends PlayerEntity> players = client.world.getPlayers();
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();

        for (PlayerEntity player : players) {
            if (player == client.player) continue;
            if (client.player.distanceTo(player) > 128) continue;

            Color drawColor = getESPColor(player);

            switch (renderMode) {
                case 1 -> draw3DBoxESP(matrices, camera, player, drawColor);
                case 2 -> applyShaderOutline(player);
            }
        }
    }

    private void draw3DBoxESP(MatrixStack matrices, Camera camera, PlayerEntity entity, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        double x = entity.getX() - camera.getPos().x;
        double y = entity.getY() - camera.getPos().y;
        double z = entity.getZ() - camera.getPos().z;

        Box box = entity.getBoundingBox();
        Box drawBox = new Box(x - (box.getXLength() / 2), y, z - (box.getZLength() / 2),
                               x + (box.getXLength() / 2), y + box.getYLength(), z + (box.getZLength() / 2));

        // Вызываем drawBox напрямую. Строку RenderSystem.setShader мы полностью убрали.
        WorldRenderer.drawBox(matrices, bufferBuilder, drawBox, color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.6f);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private void applyShaderOutline(PlayerEntity player) {
        player.setGlowing(true);
    }

    private Color getESPColor(PlayerEntity player) {
        switch (colorMode) {
            case 1:
                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float hue = Math.max(0.0F, Math.min(health / maxHealth, 1.0F)) / 3.0F;
                return Color.getHSBColor(hue, 1.0F, 1.0F);
            case 2:
                float rainbowHue = (System.currentTimeMillis() % 3000) / 3000f;
                return Color.getHSBColor(rainbowHue, 1.0F, 1.0F);
            default:
                return staticColor;
        }
    }
}
