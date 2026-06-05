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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class PulseVisualMod implements ModInitializer {
    
    private static KeyBinding configKeyBinding;
    
    public static boolean targetHud = true;
    public static boolean shockwaves = true;
    public static boolean chromaRGB = true;
    public static boolean inventoryPuller = false; // Название новой функции

    public static int hotbarSlotIndex = 0; // В какой слот хотбара призывать вещи (1-9)

    private static long lastPullTime = 0; 

    @Override
    public void onInitialize() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pulsevisual.settings", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_P, 
                "category.pulsevisual.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.wasPressed()) {
                if (client.player != null) {
                    client.openScreen(new PulseVisualScreen());
                }
            }

            // ЛОГИКА АВТО-ПОДТЯГИВАНИЯ ИЗ ИНВЕНТАРЯ
            if (inventoryPuller && client.player != null && client.interactionManager != null && client.currentScreen == null) {
                long currentTime = System.currentTimeMillis();
                
                // Проверяем настроенный слот хотбара игрока
                ItemStack hotbarStack = client.player.inventory.getStack(hotbarSlotIndex);
                
                // Если выбранный слот пустой, и прошло больше 300мс с прошлого раза
                if (hotbarStack.isEmpty() && (currentTime - lastPullTime > 300)) {
                    
                    // Сканируем только основной инвентарь (слоты от 9 до 35, исключая хотбар)
                    for (int slot = 9; slot < 36; slot++) {
                        ItemStack invStack = client.player.inventory.getStack(slot);
                        
                        // Если нашли хоть какой-то предмет в инвентаре
                        if (!invStack.isEmpty()) {
                            int targetHotbarContainerId = 36 + hotbarSlotIndex; // ID слота хотбара в контейнере
                            
                            // 1. Кликаем по найденному предмету в инвентаре, чтобы взять его на курсор
                            client.interactionManager.clickSlot(
                                client.player.currentScreenHandler.syncId, 
                                slot, 
                                0, 
                                SlotActionType.PICKUP, 
                                client.player
                            );
                            
                            // 2. Кликаем по нашему пустому слоту хотбара, чтобы положить его туда
                            client.interactionManager.clickSlot(
                                client.player.currentScreenHandler.syncId, 
                                targetHotbarContainerId, 
                                0, 
                                SlotActionType.PICKUP, 
                                client.player
                            );
                            
                            lastPullTime = currentTime;
                            break; // За один раз забираем один предмет, выходим из цикла
                        }
                    }
                }
            }
        });

        // HUD рендеринг (Оставляем оригинальные круги Pulse)
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

                if (targetHud) {
                    int lines = 45;
                    double innerRadius = 28.0;
                    for (int i = 0; i < lines; i++) {
                        double angle = (i * (360.0 / lines)) * Math.PI / 180.0;
                        double dynamicPulse = Math.sin((time / 130.0) + i * 0.35) * 5.5 + Math.cos((time / 220.0) - i * 0.15) * 3.5;
                        dynamicPulse = Math.max(1.0, dynamicPulse + 3.0);
                        double x1 = centerX + Math.cos(angle) * innerRadius;
                        double y1 = centerY + Math.sin(angle) * innerRadius;
                        double x2 = centerX + Math.cos(angle) * (innerRadius + dynamicPulse);
                        double y2 = centerY + Math.sin(angle) * (innerRadius + dynamicPulse);
                        drawPulseLine(matrixStack, x1, y1, x2, y2, r, g, b, 1.0f);
                    }
                }

                if (shockwaves) {
                    for (int step = 0; step < 3; step++) {
                        long waveOffset = time + (step * 650);
                        double radius = 12.0 + ((waveOffset % 1950) / 22.0);
                        float fadeAlpha = 1.0f - (float)((waveOffset % 1950) / 1950.0);
                        drawPulseCircle(matrixStack, centerX, centerY, radius, r, g, b, fadeAlpha);
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

    private static void drawPulseLine(MatrixStack matrices, double x1, double y1, double x2, double y2, float r, float g, float b, float a) {
        RenderSystem.lineWidth(2.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrices.peek().getPositionMatrix(), (float)x1, (float)y1, 0).color(r, g, b, a).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float)x2, (float)y2, 0).color(r, g, b, a).next();
        tessellator.draw();
    }

    private static void drawPulseCircle(MatrixStack matrices, double cx, double cy, double radius, float r, float g, float b, float a) {
        RenderSystem.lineWidth(1.5f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        int points = 64;
        for (int i = 0; i <= points; i++) {
            double theta = i * (Math.PI * 2 / points);
            double x = cx + Math.cos(theta) * radius;
            double y = cy + Math.sin(theta) * radius;
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)x, (float)y, 0).color(r, g, b, a).next();
        }
        tessellator.draw();
    }

    // СТИЛЬНОЕ СLICK GUI ОКНО С НАСТРОЙКОЙ
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
            
            // Новые строки управления
            renderModuleRow(matrices, "Inv Puller", inventoryPuller ? "§a[ON]" : "§c[OFF]", x + 15, y + 95, mouseX, mouseY);
            renderModuleRow(matrices, " Put to Slot", "§b[" + (hotbarSlotIndex + 1) + "]", x + 15, y + 115, mouseX, mouseY);
            
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
