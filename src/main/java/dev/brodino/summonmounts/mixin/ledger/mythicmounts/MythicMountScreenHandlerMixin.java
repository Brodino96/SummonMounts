package dev.brodino.summonmounts.mixin.ledger.mythicmounts;

import com.yahoo.chirpycricket.mythicmounts.entity.MountEntity;
import com.yahoo.chirpycricket.mythicmounts.screen.MountScreenHandler;
import dev.brodino.summonmounts.ledger.LedgerManager;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition("mythicmounts"))
@Mixin(ScreenHandler.class)
public class MythicMountScreenHandlerMixin {

    @Unique
    private ItemStack summonmounts$oldSaddle = ItemStack.EMPTY;
    @Unique
    private ItemStack summonmounts$oldArmor = ItemStack.EMPTY;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onSlotClick", at = @At("HEAD"))
    private void summonmounts$beforeSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!((Object) this instanceof MountScreenHandler handler)) return;
        this.summonmounts$oldSaddle = handler.getSlot(0).getStack().copy();
        this.summonmounts$oldArmor = handler.getSlot(1).getStack().copy();
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onSlotClick", at = @At("TAIL"))
    private void summonmounts$afterSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!((Object) this instanceof MountScreenHandler handler)) return;
        this.summonmounts$checkChange(player, handler.entity, this.summonmounts$oldSaddle, handler.getSlot(0).getStack());
        this.summonmounts$checkChange(player, handler.entity, this.summonmounts$oldArmor, handler.getSlot(1).getStack());
    }

    @Unique
    private void summonmounts$checkChange(PlayerEntity player, MountEntity entity, ItemStack oldStack, ItemStack newStack) {
        if (ItemStack.areEqual(oldStack, newStack)) return;

        if (!oldStack.isEmpty()) {
            Identifier itemId = Registry.ITEM.getId(oldStack.getItem());
            LedgerManager.logGear(player, false, entity, itemId);
        }

        if (!newStack.isEmpty()) {
            Identifier itemId = Registry.ITEM.getId(newStack.getItem());
            LedgerManager.logGear(player, true, entity, itemId);
        }
    }
}
