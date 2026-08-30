package dev.brodino.summonmounts.mixin;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.config.data.mounts.MountEntry;
import dev.brodino.summonmounts.items.ItemManager;
import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin {

    @Inject(method = "bondWithPlayer", at = @At("TAIL"))
    private void summonmounts$bondWithPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

        String playerName = player.getName().getString();

        AbstractHorseEntity entity = (AbstractHorseEntity) (Object) this;
        String entityId = Registry.ENTITY_TYPE.getId(entity.getType()).toString();

        SummonMounts.LOGGER.info("{} has tamed the mount: {}", playerName, entityId);

        Optional<MountEntry> mountEntry = SummonMounts.CONFIG.getData().mounts
                .stream()
                .filter(entry -> entry.id.equals(entityId))
                .findFirst();

        if (mountEntry.isEmpty()) {
            SummonMounts.LOGGER.info("Failed to find config for mount: {}", entityId);
            return;
        }

        SummonMounts.LOGGER.info("Generating ocarina for {}", playerName);

        OcarinaItem item = ItemManager.getOcarinaFromEnum(mountEntry.get().ocarina);
        ItemStack stack = new ItemStack(item);
        Mount.fromEntity(player, entity, stack);
        player.giveItemStack(stack);
    }

    /**
     * @author Brodino
     * @reason New feature
     */
    @Overwrite
    public boolean receiveFood(PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Optional<Mount> mount = MountManager.getMountFromEntity((AbstractHorseEntity) (Object) this);
        if (mount.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        String itemId = Registry.ITEM.getId(item).toString();
        Float repair = SummonMounts.CONFIG.getFoodRepair(itemId);
        if (repair == null) {
            return false;
        }

        return mount.get().repairItem(repair);
    }
}
