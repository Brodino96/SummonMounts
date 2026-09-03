package dev.brodino.summonmounts;

import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Utils {

    private static final ParticleType<?> DEFAULT_PARTICLES = ParticleTypes.WITCH;

    public static boolean combatLogCheck(PlayerEntity player) {
        return SummonMounts.COMBATLOG_PRESENT && TagData.getCombat((IEntityDataSaver) player);
    }

    public static void notifyPlayer(PlayerEntity player, Text text) { player.sendMessage(text, true); }
    public static void notifyPlayer(PlayerEntity player, String translatableKey) { notifyPlayer(player, Text.translatable(translatableKey));}

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerPlayerEntity player, Vec3d pos) {
        return player.getWorld().getPlayers(p -> p.squaredDistanceTo(pos) < (32 * 32)); // Same distance as world.spawnParticles
    }

    public static ParticleType<?> getPlayerParticles(PlayerEntity player, ItemStack stack) {
        if (!Permissions.check(player, "summonmounts.custom_paricles", 2)) {
            return DEFAULT_PARTICLES;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("Color")) return DEFAULT_PARTICLES;

        DyeColor color = DyeColor.byFireworkColor(nbt.getInt("Color"));
        if (color == null) return DEFAULT_PARTICLES;

        return switch (color) {
            case PURPLE -> ParticleTypes.WITCH;
            default -> DEFAULT_PARTICLES;
        };
    }
}
