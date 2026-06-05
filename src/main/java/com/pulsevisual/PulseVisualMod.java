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
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class PulseVisualMod implements ModInitializer {
    
    private static KeyBinding configKeyBinding;
    private static KeyBinding swapKeyBinding; 
    
    public static boolean targetHud = true;
    public static boolean shockwaves = true;
    public static boolean chromaRGB = true;
    public static boolean inventoryPuller = true; 

    public static int hotbarSlotIndex = 0; 

    @Override
    public void onInitialize() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.settings", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_P, 
                "category.pulsevisual.general"
        ));

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

            // ИСПРАВЛЕННАЯ ЛОГИКА СВАПА С СЕТЕВЫМИ КЛИКАМИ И ЧАТОМ
            if (inventoryPuller && client.currentScreen == null) {
                while (swapKeyBinding.wasPressed()) {
                    
                    client.player.sendMessage(new LiteralText("§b[Pulse] Кнопка R нажата!"), false);
                    
                    boolean itemFound = false;
                    
                    // В Майнкрафте слоты инвентаря в контейнере (window ID 0) идут так:
                    // Слоты 9-35 (основной инвентарь) — это ID с 9 по 35.
                    // Слоты 0-8 (хотбар) — это ID с 36 по 44.
                    for (int slot = 9; slot < 36; slot++) {
                        if (!client.player.inventory.getStack(slot).isEmpty()) {
                            
                            int syncId = client.player.currentScreenHandler.syncId;
                            int targetHotbarId = 36 + hotbarSlotIndex; 
                            
                            client.player.sendMessage(new LiteralText("§e[Pulse] Найдена вещь в слоте: " + slot + ". Меняю со слотом хотбара: " + (hotbarSlotIndex + 1)), false);

                            // Используем прямой метод clickSlot, но с эмуляцией кликов мыши (0 = ЛКМ)
                            // 1. Берем вещь из инвентаря
                            client.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, client.player);
                            
                            // 2. Кладем её в хотбар (и забираем то, что там было на курсор)
                            client.interactionManager.clickSlot(syncId, targetHotbarId, 0, SlotActionType.PICKUP, client.player);
                            
                            // 3. Возвращаем старую вещь в инвентарь
                            client.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, client.player);
                            
                            itemFound = true;
                            break; 
                        }
                    }
                    
                    if (!itemFound) {
                        client.player.sendMessage(new LiteralText("§c[Pulse] Ошибка: В основном инвентаре (вверху) нет вещей для обмена!"), false);
                    }
                }
            }
        });

        // HUD рендеринг
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.textRenderer != null && !client.options.hudHidden) {
                long time = System.currentTimeMillis();
                int centerX = client.getWindow().getScaledWidth() / 2;
                int centerY = client.getWindow().getScaledHeight() / 2;

                int rgb = java.awt.Color.HSBtoRGB((time % 4000) / 4000f, 0.75f, 1f);
                float r = ((rgb >> 16) & 0xFF) / 255f;
                float g = ((rgb >> 8) & 0xFF) / 255f;
                float b = (rgb & 0xFF) / 255f;

                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.defaultBlendFunc();

                Matrix4f matrix = matrixStack.peek().getModel();

                if (targetHud) {
                    int lines = 45;
                    double innerRadius = 28.0;
                    RenderSystem.lineWidth(2.0f);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(1, VertexFormats.POSITION_COLOR);

                    for (int i = 0; i < lines; i++) {
                        double angle = (i * (360.0 / lines)) * Math.PI / 180.0;
                        double dynamicPulse = Math.sin((time / 130.0) + i * 0.35) * 5.5 + Math.cos((time / 220.0) - i * 0.15) * 3.5;
                        dynamicPulse = Math.max(1.0, dynamicPulse + 3.0);
                        
                        double x1 = centerX + Math.cos(angle) * innerRadius;
                        double y1 = centerY + Math.sin(angle) * innerRadius;
                        double x2 = centerX + Math.cos(angle) * (innerRadius + dynamicPulse);
                        double y2 = centerY + Math.sin(angle) * (innerRadius + dynamicPulse);

                        buffer.vertex(matrix, (float)x1, (float)y1, 0).color(r, g, b, 1.0f).next();
                        buffer.vertex(matrix, (float)x2, (float)y2, 0).color(r, g, b, 1.0f).next();
                    }
                    tessellator.draw();
                }

                if (shockwaves) {
                    RenderSystem.lineWidth(1.5f);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();

                    for (int step = 0; step < 3; step++) {
                        long waveOffset = time + (step * 650);
                        double radius = 12.0 + ((waveOffset % 1950) / 22.0);
                        float fadeAlpha = 1.0f - (float)((waveOffset % 1950) / 1950.0);

                        buffer.begin(3, VertexFormats.POSITION_COLOR);
                        int points = 64;
                        for (int i = 0; i <= points; i++) {
                            double theta = i * (Math.PI * 2 / points);
                            double x = centerX + Math.cos(theta) * radius;
                            double y = centerY + Math.sin(theta) * radius;
                            buffer.vertex(matrix, (float)x, (float)y, 0).color(r, g, b, fadeAlpha).next();
                        }
                        tessellator.draw();
                    }
                }

                RenderSystem.enableTexture();
                RenderSystem.disableBlend();

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
            int guiHeight = 145; 
            int x = this.width / 2 - guiWidth / 2;
            int y = this.height / 2 - guiHeight / 2;

            DrawableHelper.fill(matrices, x, y, x + guiWidth, y + guiHeight, 0xD0101014);
            drawGuiBorder(matrices, x, y, guiWidth, guiHeight, rgb);

            drawCenteredText(matrices, this.textRenderer, "PULSE VISUAL", x + guiWidth / 2, y + 8, rgb);
            DrawableHelper.fill(matrices, x + 10, y + 20, x + guiWidth - 10, y + 21, 0x40FFFFFF);

            renderModuleRow(matrices, "TargetHUD", targetHud ? "§a[ON]" : "§c[OFF]", x + 15, y + 30, mouseX, mouseY);
            renderModuleRow(matrices, "Shockwaves", shockwaves ? "§a[ON]" : "§c[OFF]", x + 15, y + 50, mouseX, mouseY);
            renderModuleRow(matrices, "Chroma RGB", chromaRGB ? "§a[ON]" : "§c[OFF]", x + 15, y + 70, mouseX, mouseY);
            renderModuleRow(matrices, "Inv Swapper", inventoryPuller ? "§a[ON]" : "§c[OFF]", x + 15, y + 95, mouseX, mouseY);
            renderModuleRow(matrices, " Target Slot", "§b[" + (hotbarSlotIndex + 1) + "]", x + 15, y + 115, mouseX, mouseY);
            
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int guiWidth = 160;
            int x = this.width / 2 - guiWidth / 2;
            int y = this.height / 2 - 145 / 2;

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
            if (isHovered(mouseX, mouseY, x + 10, y + 113, guiWidth - 20, 14)) {
                hotbarSlotIndex = (hotbarSlotIndex + 1) % 9;
                return true;
            }

            return super.mouse
