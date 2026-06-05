package com.pulsevisual;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class PulseVisualMod implements ModInitializer {
    
    // Создаем кнопку вызова GUI (по умолчанию на английскую букву P)
    private static KeyBinding configKeyBinding;

    @Override
    public void onInitialize() {
        // 1. Регистрируем клавишу в настройках игры
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.settings", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_P, // Буква P
                "category.pulsevisual.general"
        ));

        // 2. Отслеживаем нажатие кнопки каждый тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.wasPressed()) {
                // Если игрок в игре и нажал P — открываем наше GUI
                if (client.player != null) {
                    client.openScreen(new PulseVisualScreen());
                }
            }
        });

        // 3. Наш старый добрый рендер плашки на экране
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                DrawableHelper.fill(matrixStack, 5, 5, 145, 25, 0x80000000);
                client.textRenderer.draw(matrixStack, "§bPulse Visual §a[Active]", 10, 10, 0xffffff);
            }
        });
    }

    // Внутренний класс самого экрана GUI
    public static class PulseVisualScreen extends Screen {
        public PulseVisualScreen() {
            super(new LiteralText("Pulse Visual Settings"));
        }

        @Override
        protected void init() {
            // Добавляем кнопку "Закрыть меню" по центру экрана
            this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 2, 200, 20, new LiteralText("Close Menu"), button -> {
                this.client.openScreen(null); // Закрывает GUI и возвращает в игру
            }));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices); // Затемняем фон Майнкрафта
            
            // Рисуем заголовок меню по центру
            drawCenteredText(matrices, this.textRenderer, "§bPulse Visual §fSettings Menu", this.width / 2, this.height / 2 - 40, 0xffffff);
            
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
