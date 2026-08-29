package dev.brodino.summonmounts;

import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Utils {

    public static boolean combatLogCheck(PlayerEntity player) {
        return SummonMounts.COMBATLOG_PRESENT && TagData.getCombat((IEntityDataSaver) player);
    }

    public static void notifyPlayer(PlayerEntity player, Text text) { player.sendMessage(text, true); }
    public static void notifyPlayer(PlayerEntity player, String translatableKey) { notifyPlayer(player, Text.translatable(translatableKey));}
}
