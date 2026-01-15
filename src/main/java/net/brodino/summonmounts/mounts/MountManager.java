package net.brodino.summonmounts.mounts;

import net.brodino.summonmounts.SummonMounts;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MountManager {

    private static final Map<UUID, UUID> PLAYER_MOUNTS = new HashMap<>();

    public static boolean bindMountToItem(AbstractHorseEntity entity, ItemStack stack, PlayerEntity player) {

        String mountId = Registry.ENTITY_TYPE.getId(entity.getType()).toString();

        if (!SummonMounts.CONFIG.getAllowedMounts().contains(mountId)) {
            return false;
        }


        return true;
    }
}
