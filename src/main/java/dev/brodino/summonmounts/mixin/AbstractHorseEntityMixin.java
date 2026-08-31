package dev.brodino.summonmounts.mixin;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.Utils;
import dev.brodino.summonmounts.config.data.MountEntry;
import dev.brodino.summonmounts.items.ItemManager;
import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "putPlayerOnBack", at = @At("HEAD"), cancellable = true)
    public void putPlayerOnBack(PlayerEntity player, CallbackInfo ci) {
        AbstractHorseEntity entity = (AbstractHorseEntity) (Object) this;

        Optional<Mount> mount = MountManager.getMountFromEntity(entity);
        if (mount.isEmpty()) {
            return;
        }

        if (!mount.get().isMountable()) {
            Utils.notifyPlayer(player, Text.translatable("feedback.summonmounts.mount.cannot_mount"));
            ci.cancel();
        }
    }
}
