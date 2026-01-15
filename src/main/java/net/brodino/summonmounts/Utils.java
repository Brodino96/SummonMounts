package net.brodino.summonmounts;

import net.minecraft.entity.player.PlayerEntity;

public class Utils {

    public static String getPlayerName(PlayerEntity player) {
        return player.getDisplayName().getString();
    }
}
