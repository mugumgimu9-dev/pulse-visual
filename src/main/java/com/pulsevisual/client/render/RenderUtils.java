package com.pulsevisual.client.render;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class RenderUtils {

    public static void drawString(MatrixStack matrices, String text, int x, int y, int color) {
        // Draw text on screen
    }

    public static void drawRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        // Draw filled rectangle
    }

    public static void drawBorderedRect(MatrixStack matrices, int x, int y, int width, int height, 
                                        int fillColor, int borderColor, int borderWidth) {
        // Draw rectangle with border
    }

    public static void drawLine(MatrixStack matrices, float x1, float y1, float x2, float y2, int color) {
        // Draw line
    }

    public static void drawCircle(MatrixStack matrices, int x, int y, int radius, int color) {
        // Draw circle
    }

    public static void drawBox(MatrixStack matrices, double x, double y, double z, 
                               double width, double height, double length, int color) {
        // Draw 3D box for ESP
    }
}
