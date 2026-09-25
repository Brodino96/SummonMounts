package dev.brodino.summonmounts.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.brodino.summonmounts.MountManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/tag/TagKey;)Z"))
    private boolean summonmounts$suffocateAboveHeightLimit(boolean original) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            return (original || this.summonmounts$isAboveHeightLimit(player)) && !player.hasPermissionLevel(2);
        }
        return false;
    }

    // This prevents the player from being kicked from the mount because "is underwater", thanks minecraft
    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canBeRiddenInWater()Z"))
    private boolean summonmounts$keepRidingAboveHeightLimit(boolean original) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            return (original || this.summonmounts$isAboveHeightLimit(player)) && !player.hasPermissionLevel(2);
        }
        return false;
    }

    @Unique
    private boolean summonmounts$isAboveHeightLimit(ServerPlayerEntity player) {
        return MountManager.isAboveHeightLimit(player);
    }
}
