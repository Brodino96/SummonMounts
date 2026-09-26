package dev.brodino.summonmounts.mixin.ledger;

import dev.brodino.summonmounts.ledger.LedgerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Unique
    private ItemStack oldSaddle = ItemStack.EMPTY;
    @Unique
    private ItemStack oldArmor = ItemStack.EMPTY;

    @Inject(method = "onSlotClick", at = @At("HEAD"))
    private void summonmounts$beforeSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if ((Object) this instanceof HorseScreenHandler handler) {
            this.oldSaddle = handler.getSlot(0).getStack().copy();
            this.oldArmor = handler.getSlot(1).getStack().copy();
        }
    }

    @Inject(method = "onSlotClick", at = @At("TAIL"))
    private void summonmounts$afterSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if ((Object) this instanceof HorseScreenHandler handler) {
            ItemStack newSaddle = handler.getSlot(0).getStack().copy();
            ItemStack newArmor = handler.getSlot(1).getStack().copy();

            this.summonmounts$checkChange(player, handler, this.oldSaddle, newSaddle);
            this.summonmounts$checkChange(player, handler, this.oldArmor, newArmor);
        }
    }

    @Unique
    private void summonmounts$checkChange(PlayerEntity player, HorseScreenHandler handler, ItemStack oldStack, ItemStack newStack) {
        if (oldStack.isEmpty() && newStack.isEmpty()) return;

        if (oldStack.isEmpty()) {
            Identifier itemId = Registry.ITEM.getId(newStack.getItem());
            LedgerManager.logGear(player, true, ((HorseScreenHandlerAccessor) handler).getEntity(), itemId);
            return;
        }

        if (newStack.isEmpty()) {
            Identifier itemId = Registry.ITEM.getId(oldStack.getItem());
            LedgerManager.logGear(player, false, ((HorseScreenHandlerAccessor) handler).getEntity(), itemId);
            return;
        }

        if (!ItemStack.areEqual(oldStack, newStack)) {
            Identifier newItemId = Registry.ITEM.getId(newStack.getItem());
            Identifier oldItemId = Registry.ITEM.getId(oldStack.getItem());

            LedgerManager.logGear(player, true, ((HorseScreenHandlerAccessor) handler).getEntity(), newItemId);
            LedgerManager.logGear(player, false, ((HorseScreenHandlerAccessor) handler).getEntity(), oldItemId);
        }
    }
}
