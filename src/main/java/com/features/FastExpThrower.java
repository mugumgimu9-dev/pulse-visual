package com.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class FastExpThrower {
    
    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null || client.interactionManager == null) return;

        if (client.player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE) {
            if (client.options.keyUse.isPressed()) {
                client.interactionManager.interactItem(client.player, client.world, Hand.MAIN_HAND);
            }
        }
    }
}
