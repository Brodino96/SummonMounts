package dev.brodino.summonmounts.mixin;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.OcarinaManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class DropItemMixin {

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        PlayerInventory inventory = player.getInventory();
        ItemStack stack = inventory.getMainHandStack();

        if (!OcarinaManager.isOcarina(stack.getItem())) {
            return;
        }

        if (stack.getNbt() != null && stack.getNbt().contains(SummonMounts.MOD_ID)) {
            if (MountManager.hasActiveMount(player)) {
                SummonMounts.LOGGER.info("Stopping {} from dropping ocarina, has an active mount", player.getName().getString());
                cir.setReturnValue(false);
                cir.cancel();
                player.networkHandler.sendPacket(new InventoryS2CPacket(player.currentScreenHandler.syncId, player.currentScreenHandler.getRevision(), player.currentScreenHandler.getStacks(), player.currentScreenHandler.getCursorStack()));
            }
        }
    }
}