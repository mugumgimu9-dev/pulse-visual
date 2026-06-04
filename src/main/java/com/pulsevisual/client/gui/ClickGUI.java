package com.pulsevisual.client.gui;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClickGUI extends Screen {
    private int theme = 0; // 0 = dark, 1 = light
    private boolean isOpen = false;

    public ClickGUI(Text component) {
        super(component);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public void toggleOpen() {
        isOpen = !isOpen;
    }
}
