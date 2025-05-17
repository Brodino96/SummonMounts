package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.MountManager;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class TameEntityMixin {

    public TameEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super();
        SummonMounts.LOGGER.info("TameEntityMixin");
    }

    @Inject(method = "bondWithPlayer", at = @At("TAIL"))
    private void bondWithPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

        String playerName = player.getDisplayName().getString();
        SummonMounts.LOGGER.info("{} has bond with a mount", playerName);

        AbstractHorseEntity entity = (AbstractHorseEntity) (Object) this;
        
        ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem())));
        MountManager.bindMountToItem(player, entity, stack);
        player.giveItemStack(stack);
    }
}
