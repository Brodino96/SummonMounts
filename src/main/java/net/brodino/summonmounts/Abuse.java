package net.brodino.summonmounts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class Abuse {

    public static boolean canHeDoThat(PlayerEntity player, ItemStack stack) {
        if (!player.hasVehicle()) {
            return true;
        }

        Entity vehicle = player.getVehicle();

        if (vehicle == null) {
            return true;
        }

        String mountId = Registry.ENTITY_TYPE.getId(vehicle.getType()).toString();
        boolean isMount = false;
        for (String allowedType : SummonMounts.CONFIG.allowedSummons()) {
            if (allowedType.equals(mountId)) {
                isMount = true;
            }
        }

        String itemId = Registry.ITEM.getId(stack.getItem()).toString();
        if (isMount && SummonMounts.CONFIG.blockedItems().contains(itemId)) {
            return false;
        }

        return true;
    }
}
