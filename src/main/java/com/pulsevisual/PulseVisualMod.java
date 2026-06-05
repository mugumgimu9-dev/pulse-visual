package com.pulsevisual;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class PulseVisualMod implements ModInitializer {
    
    private static KeyBinding configKeyBinding;
    private static KeyBinding swapKeyBinding; 
    
    public static boolean targetHud = true;
    public static boolean shockwaves = true;
    public static boolean chromaRGB = true;
    public static boolean inventoryPuller = true; 

    @Override
    public void onInitialize() {
        // Кнопка меню (P)
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.settings", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_P, 
                "category.pulsevisual.general"
        ));

        // Кнопка для свапа во вторую руку (R)
        swapKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.swap", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_R, 
                "category.pulsevisual.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null) return;

            while (configKeyBinding.wasPressed()) {
                client.openScreen(new PulseVisualScreen());
            }

            // ЛОГИКА СВАПА ВО ВТОРУЮ РУКУ ПО НАЖАТИЮ R
            if (inventoryPuller && client.currentScreen == null) {
                while (swapKeyBinding.wasPressed()) {
                    
                    client.player.sendMessage(new LiteralText("§b[Pulse] Кнопка R нажата!"), false);
                    boolean itemFound = false;
                    
                    // Сканируем весь инвентарь (слоты 9-35 и хотбар 0-8, который в контейнере имеет ID 36-44)
                    // Сначала проверим хотбар, потом основной инвентарь
                    for (int slot = 9; slot < 45; slot++) {
                        if (!client.player.inventory.getStack(slot < 36 ? slot : slot - 36).isEmpty()) {
                            
                            int syncId = client.player.currentScreenHandler.syncId;
                            int offHandSlotId = 45; // ID слота левой руки в Майнкрафте
                            
                            client.player.sendMessage(new LiteralText("§e[Pulse] Предмет отправлен во вторую руку!"), false);

                            // 1. Берем найденный предмет из инвентаря на курсор
                            client.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, client.player);
                            // 2. Кладем его во вторую руку (и забираем то, что там было, на курсор)
                            client.interactionManager.clickSlot(syncId, offHandSlotId, 0, SlotActionType.PICKUP, client.player);
                            // 3. Возвращаем старый предмет из левой руки на пустое место в инвентаре
                            client.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, client.player);
                            
                            itemFound = true;
                            break; 
                        }
                    }
                    
                    if (!itemFound) {
                        client.player.sendMessage(new LiteralText("§c[Pulse] Ошибка: Инвентарь полностью пуст!"), false);
                    }
                }
            }
        });

        // Визуальный HUD
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                long time = System.currentTimeMillis();
                int rgb = java.awt.Color.HSBtoRGB((time % 4000) / 4000f, 0.75f, 1f);

                DrawableHelper.fill(matrixStack, 6, 6, 120, 24, 0xAA000000);
                DrawableHelper.fill(matrixStack, 6, 6, 8, 24, rgb);
                client.textRenderer.draw(matrixStack, "PULSE CLIENT", 14, 10, rgb);
            }
        });
    }

    // CLICK GUI ОКНО
    public static class PulseVisualScreen extends Screen {
        public PulseVisualScreen() {
            super(new LiteralText("Pulse ClickGUI"));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);

            long time = System.currentTimeMillis();
            int rgb = java.awt.Color.HSBtoRGB((time % 4000) / 4000f, 0.75f, 1f);

            int guiWidth = 160;
            int guiHeight = 125; 
            int x = this.width / 2 - guiWidth / 2;
            int y = this.height / 2 - guiHeight / 2;

            DrawableHelper.fill(matrices, x, y, x + guiWidth, y + guiHeight, 0xD0101014);
            drawGuiBorder(matrices, x, y, guiWidth, guiHeight, rgb);

            drawCenteredText(matrices, this.textRenderer, "PULSE VISUAL", x + guiWidth / 2, y + 8, rgb);
            DrawableHelper.fill(matrices, x + 10, y + 20, x + guiWidth - 10, y + 21, 0x40FFFFFF);

            renderModuleRow(matrices, "TargetHUD", targetHud ? "§a[ON]" : "§c[OFF]", x + 15, y + 30, mouseX, mouseY);
            renderModuleRow(matrices, "Shockwaves", shockwaves ? "§a[ON]" : "§c[OFF]", x + 15, y + 50, mouseX, mouseY);
            renderModuleRow(matrices, "Chroma RGB", chromaRGB ? "§a[ON]" : "§c[OFF]", x + 15, y + 70, mouseX, mouseY);
            renderModuleRow(matrices, "Offhand Swap", inventoryPuller ? "§a[ON]" : "§c[OFF]", x + 15, y + 95, mouseX, mouseY);
            
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int guiWidth = 160;
            int x = this.width / 2 - guiWidth / 2;
            int y = this.height / 2 - 125 / 2;

            if (isHovered(mouseX, mouseY, x + 10, y + 28, guiWidth - 20, 14)) {
                targetHud = !targetHud;
                return true;
            }
            if (isHovered(mouseX, mouseY, x + 10, y + 48, guiWidth - 20, 14)) {
                shockwaves = !shockwaves;
                return true;
            }
            if (isHovered(mouseX, mouseY, x + 10, y + 68, guiWidth - 20, 14)) {
                chromaRGB = !chromaRGB;
                return true;
            }
            if (isHovered(mouseX, mouseY, x + 10, y + 93, guiWidth - 20, 14)) {
                inventoryPuller = !inventoryPuller;
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        private void renderModuleRow(MatrixStack matrices, String name, String statusText, int x, int y, int mouseX, int mouseY) {
            int rowWidth = 130;
            int rowHeight = 14;
            boolean hovered = isHovered(mouseX, mouseY, x - 5, y - 2, rowWidth, rowHeight);
            if (hovered) {
                DrawableHelper.fill(matrices, x - 5, y - 2, x - 5 + rowWidth, y - 2 + rowHeight, 0x20FFFFFF);
            }
            this.textRenderer.draw(matrices, name, x, y, 0xFFFFFF);
            this.textRenderer.draw(matrices, statusText, x + rowWidth - 30, y, 0xFFFFFF);
        }

        private void drawGuiBorder(MatrixStack matrices, int x, int y, int w, int h, int color) {
            DrawableHelper.fill(matrices, x, y, x + w, y + 1, color);
            DrawableHelper.fill(matrices, x, y + h - 1, x + w, y + h, color);
            DrawableHelper.fill(matrices, x, y, x + 1, y + h, color);
            DrawableHelper.fill(matrices, x + w - 1, y, x + w, y + h, color);
        }

        private boolean isHovered(double mx, double my, int x, int y, int w, int h) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }
}
