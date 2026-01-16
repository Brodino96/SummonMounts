package net.brodino.summonmounts.mounts;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MountManager {

    private static final Map<UUID, UUID> PLAYER_MOUNTS = new HashMap<>();

    public static boolean bindMountToItem(AbstractHorseEntity entity, ItemStack stack, PlayerEntity player) {

        Mount mount = Mount.fromEntity(entity);
        mount.dismiss();

        return true;
    }
}
