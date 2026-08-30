package dev.brodino.summonmounts;

import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class Utils {

    private static final ParticleType<?> DEFAULT_PARTICLE = ParticleTypes.WITCH;

    public static boolean combatLogCheck(PlayerEntity player) {
        return SummonMounts.COMBATLOG_PRESENT && TagData.getCombat((IEntityDataSaver) player);
    }

    public static void notifyPlayer(PlayerEntity player, Text text) { player.sendMessage(text, true); }
    public static void notifyPlayer(PlayerEntity player, String translatableKey) { notifyPlayer(player, Text.translatable(translatableKey));}

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerPlayerEntity player, Vec3d pos) {
        return player.getWorld().getPlayers(p -> p.squaredDistanceTo(pos) < (32 * 32)); // Same distance as world.spawnParticles
    }

    public static ParticleType<?> getPlayerParticles(PlayerEntity player) {
        NbtCompound nbt = new NbtCompound();
        player.writeNbt(nbt);
        if (!nbt.contains(SummonMounts.MOD_ID)) {
            return DEFAULT_PARTICLE;
        }

        NbtCompound modNbt = nbt.getCompound(SummonMounts.MOD_ID);
        if (!modNbt.contains("Particles")) {
            return DEFAULT_PARTICLE;
        }

        ParticleType<?> particle = Registry.PARTICLE_TYPE.get(new Identifier(modNbt.getString("Particles")));
        if (particle == null) {
            return DEFAULT_PARTICLE;
        }

        return particle;
    }
}
