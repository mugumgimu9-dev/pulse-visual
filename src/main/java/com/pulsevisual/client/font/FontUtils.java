package com.pulsevisual.client.font;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class FontUtils {

    public static void drawString(TextRenderer renderer, String text, float x, float y, int color) {
        if (renderer == null || text == null) return;
        
        // В 1.16.5 обязательно передаем MatrixStack первым аргументом
        MatrixStack matrixStack = new MatrixStack();
        renderer.draw(matrixStack, text, x, y, color);
    }
}
