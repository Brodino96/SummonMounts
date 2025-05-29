package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.MountManager;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class DropItemMixin {

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        PlayerInventory inventory = player.getInventory();
        ItemStack stack = inventory.getMainHandStack();

        Identifier itemIdentifier = Registry.ITEM.getKey(stack.getItem()).get().getValue();
        if (!itemIdentifier.equals(new Identifier(SummonMounts.CONFIG.summonItem()))) {
            return;
        }

        if (!stack.hasNbt() || !stack.getNbt().contains("mount.genericData")) {
            return;
        }

        UUID playerUUID = player.getUuid();
        if (MountManager.hasActiveMount(playerUUID, stack)) {
            SummonMounts.LOGGER.info("{} tried to drop a mount item", player.getDisplayName().getString());
            cir.setReturnValue(false);
            cir.cancel();
            player.networkHandler.sendPacket(new InventoryS2CPacket(player.currentScreenHandler.syncId, player.currentScreenHandler.getRevision(), player.currentScreenHandler.getStacks(), player.currentScreenHandler.getCursorStack()));
        }
    }
}