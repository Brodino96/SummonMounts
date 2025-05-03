package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.MountManager;
import net.brodino.summonmounts.Summonmounts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    
    /**
     * Mixin to handle entity removal when a player disconnects
     * This ensures that mounts are properly removed when their owner logs off
     */
    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        
        // If a player is removed and it's not due to changing dimensions
        if (entity instanceof PlayerEntity && reason != Entity.RemovalReason.CHANGED_DIMENSION) {
            PlayerEntity player = (PlayerEntity)entity;
            
            // If we're on the server side, handle the player disconnect
            if (!player.world.isClient) {
                // The actual disconnect handling is in MountManager
                // This is just a backup in case the event doesn't fire
                if (Summonmounts.SERVER != null) {
                    MountManager.playerDisconnected((net.minecraft.server.network.ServerPlayerEntity)player);
                }
            }
        }
    }
}