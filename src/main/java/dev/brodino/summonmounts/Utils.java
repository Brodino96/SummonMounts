package dev.brodino.summonmounts;

import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Utils {

    public static boolean combatLogCheck(PlayerEntity player) {
        return SummonMounts.COMBATLOG_PRESENT && TagData.getCombat((IEntityDataSaver) player);
    }

    public static void notifyPlayer(PlayerEntity player, Text text) { player.sendMessage(text, true); }
    public static void notifyPlayer(PlayerEntity player, String translatableKey) { notifyPlayer(player, Text.translatable(translatableKey));}

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerPlayerEntity player, Vec3d pos) {
        return player.getWorld().getPlayers(p -> p.squaredDistanceTo(pos) < (32 * 32)); // Same distance as world.spawnParticles
    }
}
