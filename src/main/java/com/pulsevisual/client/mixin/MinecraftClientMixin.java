package com.pulsevisual.client.mixin;

import com.features.FriendManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow public HitResult crosshairTarget;

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfo ci) {
        if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) this.crosshairTarget).getEntity();
            
            if (entity instanceof PlayerEntity) {
                String name = entity.getName().getString();
                if (FriendManager.isFriend(name)) {
                    ci.cancel();
                }
            }
        }
    }
}
