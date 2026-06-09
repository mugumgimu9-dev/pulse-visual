package com.pulsevisual.client.mixin;

import com.features.FriendManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, CallbackInfo ci) {
        if (message.startsWith(".friends ")) {
            ci.cancel();
            
            String[] args = message.split(" ");
            if (args.length >= 3) {
                String action = args[1];
                String nickname = args[2];
                
                if (action.equalsIgnoreCase("add")) {
                    FriendManager.addFriend(nickname);
                    if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
                        net.minecraft.client.MinecraftClient.getInstance().player.sendMessage(
                            new LiteralText("§a[Pulse] " + nickname + " добавлен в список друзей!"), false
                        );
                    }
                } else if (action.equalsIgnoreCase("del") || action.equalsIgnoreCase("remove")) {
                    FriendManager.removeFriend(nickname);
                    if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
                        net.minecraft.client.MinecraftClient.getInstance().player.sendMessage(
                            new LiteralText("§c[Pulse] " + nickname + " удален из друзей."), false
                        );
                    }
                }
            }
        }
    }
}
