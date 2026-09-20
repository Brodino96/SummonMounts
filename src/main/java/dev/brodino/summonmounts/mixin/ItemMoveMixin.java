package dev.brodino.summonmounts.mixin;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.OcarinaItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ItemMoveMixin {

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {

        ScreenHandler screenHandler = (ScreenHandler) (Object) this;

        if (slotIndex < 0 || slotIndex >= screenHandler.slots.size()) {
            return;
        }

        ItemStack stack = screenHandler.slots.get(slotIndex).getStack();
        if (!(stack.getItem() instanceof OcarinaItem)) {
            return;
        }

        if (stack.getNbt() != null && stack.getNbt().contains(SummonMounts.MOD_ID)) {
            if (MountManager.hasActiveMount(player)) {
                SummonMounts.LOGGER.info("Stopping {} from moving ocarina inside inventory, has active mount", player.getName().getString());
                ci.cancel();
            }
        }
    }
}