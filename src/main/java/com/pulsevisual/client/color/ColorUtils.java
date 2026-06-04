package com.pulsevisual.client.color;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class ColorUtils {

    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int getBlue(int color) {
        return color & 0xFF;
    }

    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }

    public static int interpolate(int color1, int color2, float progress) {
        int r = (int) (getRed(color1) * (1 - progress) + getRed(color2) * progress);
        int g = (int) (getGreen(color1) * (1 - progress) + getGreen(color2) * progress);
        int b = (int) (getBlue(color1) * (1 - progress) + getBlue(color2) * progress);
        int a = (int) (getAlpha(color1) * (1 - progress) + getAlpha(color2) * progress);
        return rgba(r, g, b, a);
    }
}
