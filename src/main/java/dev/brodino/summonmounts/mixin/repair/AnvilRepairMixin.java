package dev.brodino.summonmounts.mixin.repair;

import dev.brodino.summonmounts.items.OcarinaItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilRepairMixin {

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void summonmounts$updateResult(CallbackInfo ci) {
        AnvilScreenHandler anvil = (AnvilScreenHandler) (Object) this;

        DefaultedList<ItemStack> stacks = anvil.getStacks();
        if (stacks.get(0).getItem() instanceof OcarinaItem && stacks.get(1).getItem() instanceof OcarinaItem) {
            ci.cancel();
        }
    }
}
