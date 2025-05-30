package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.MountManager;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.UUID;

@Mixin(ScreenHandler.class)
public class ItemMoveMixin {

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {

        ScreenHandler screenHandler = (ScreenHandler) (Object) this;

        if (slotIndex < 0 || slotIndex >= screenHandler.slots.size()) {
            return;
        }

        ItemStack stack = screenHandler.slots.get(slotIndex).getStack();
        if (stack.isEmpty()) {
            return;
        }

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));
        if (stack.getItem() != summonItem) {
            return;
        }

        if (!stack.hasNbt() || !stack.getNbt().contains("mount.genericData")) {
            return;
        }

        UUID playerUUID = player.getUuid();
        if (MountManager.hasActiveMount(playerUUID, stack)) {
            SummonMounts.LOGGER.info("{} tried to move a bound item in his inventory", player.getDisplayName().getString());
            ci.cancel();
        }

    }
}