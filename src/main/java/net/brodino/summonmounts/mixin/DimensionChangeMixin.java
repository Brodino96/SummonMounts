package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.MountManager;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class DimensionChangeMixin {

    public DimensionChangeMixin() {
        SummonMounts.LOGGER.info("DimensionChangeMixin");
    }

    @Inject(method = "removePlayer", at = @At("HEAD"))
    private void removePlayer(ServerPlayerEntity player, Entity.RemovalReason reason, CallbackInfo ci) {
        SummonMounts.LOGGER.info("{} changed dimension", player.getDisplayName().getString());
        MountManager.playerDisconnected(player);
    }
}