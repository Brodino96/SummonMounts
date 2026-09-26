package dev.brodino.summonmounts.mixin;

import dev.brodino.summonmounts.ledger.LedgerManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SaddleItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SaddleItem.class)
public class SaddleItemMixin {

	@Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Saddleable;saddle(Lnet/minecraft/sound/SoundCategory;)V"))
	public void summonmounts$useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		LedgerManager.logSaddle(user, true, entity);
	}
}
