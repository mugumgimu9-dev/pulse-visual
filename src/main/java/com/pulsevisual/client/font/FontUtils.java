package com.pulsevisual.client.font;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

@Environment(EnvType.CLIENT)
public class FontUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final TextRenderer renderer = client.textRenderer;

    public static int getStringWidth(String text) {
        return renderer.getWidth(text);
    }

    public static int getFontHeight() {
        return renderer.fontHeight;
    }

    public static void drawString(String text, float x, float y, int color) {
        renderer.draw(text, x, y, color);
    }
}
