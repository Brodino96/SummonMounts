package dev.brodino.summonmounts;

import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import net.minecraft.entity.player.PlayerEntity;

public class Utils {

    public static boolean combatLogCheck(PlayerEntity player) {
        return SummonMounts.COMBATLOG_PRESENT && TagData.getCombat((IEntityDataSaver) player);
    }

}
