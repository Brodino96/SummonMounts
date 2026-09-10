package dev.brodino.summonmounts;

import dev.brodino.summonmounts.items.OcarinaItem;
import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class Utils {

    private static final int DEFAULT_COLOR = DyeColor.PURPLE.getFireworkColor();

    public static boolean combatLogCheck(PlayerEntity player) {
        return SummonMounts.COMBATLOG_PRESENT && TagData.getCombat((IEntityDataSaver) player);
    }

    public static void notifyPlayer(PlayerEntity player, Text text) { player.sendMessage(text, true); }
    public static void notifyPlayer(PlayerEntity player, String translatableKey) { notifyPlayer(player, Text.translatable(translatableKey));}

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerPlayerEntity player, Vec3d pos) {
        return player.getWorld().getPlayers(p -> p.squaredDistanceTo(pos) < (32 * 32)); // Same distance as world.spawnParticles
    }

    public static int getPlayerParticleColor(PlayerEntity player, ItemStack stack) {
        if (!Permissions.check(player, "summonmounts.custom_particles", 2)) {
            return DEFAULT_COLOR;
        }

        if (stack.getItem() instanceof OcarinaItem) {
            Optional<Integer> color = OcarinaItem.getOcarinaColor(stack);
            return color.orElse(DEFAULT_COLOR);
        }

        return DEFAULT_COLOR;
    }
}
