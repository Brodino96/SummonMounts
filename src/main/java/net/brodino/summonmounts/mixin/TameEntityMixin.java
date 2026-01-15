package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.SummonMounts;
import net.brodino.summonmounts.Utils;
import net.brodino.summonmounts.mounts.Mount;
import net.brodino.summonmounts.mounts.MountManager;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class TameEntityMixin {

    public TameEntityMixin() {
        super();
        SummonMounts.LOGGER.info("Initialized TameEntityMixin");
    }

    @Inject(method = "bondWithPlayer", at = @At("TAIL"))
    private void bondWithPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        SummonMounts.LOGGER.info("{} has tamed a mount", Utils.getPlayerName(player));

        AbstractHorseEntity horseEntity = (AbstractHorseEntity) (Object) this;
        ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.getSummonItem())));

        if (MountManager.bindMountToItem(horseEntity, stack, player)) {
            player.giveItemStack(stack);
        }
    }
}
