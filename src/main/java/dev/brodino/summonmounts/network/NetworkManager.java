package dev.brodino.summonmounts.network;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.Utils;
import dev.brodino.summonmounts.client.ParticleHelper;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetworkManager {

    private static final Identifier SUMMON_PARTICLES_PACKET = new Identifier(SummonMounts.MOD_ID, "summon_particles_packet");
    private static final Identifier RECALL_PARTICLES_PACKET = new Identifier(SummonMounts.MOD_ID, "recall_particles_packet");

    public static void sendSummonParticlesPacket(ServerPlayerEntity player, ParticleType<?> particle, Mount mount) {
        for (final ServerPlayerEntity target : Utils.getNearbyPlayers(player, mount.getPos())) {
            ServerPlayNetworking.send(target, SUMMON_PARTICLES_PACKET, ParticleHolder.fromMount(particle, mount).getBuf());
        }
    }

    public static void sendRecallParticlesPacket(ServerPlayerEntity player, ParticleType<?> particle, Mount mount) {
        for (final ServerPlayerEntity target : Utils.getNearbyPlayers(player, mount.getPos())) {
            ServerPlayNetworking.send(target, RECALL_PARTICLES_PACKET, ParticleHolder.fromMount(particle, mount).getBuf());
        }
    }

    public static void registerClientPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SUMMON_PARTICLES_PACKET, NetworkManager::summonParticlesPacketHandler);
        ClientPlayNetworking.registerGlobalReceiver(RECALL_PARTICLES_PACKET, NetworkManager::recallParticlesPacketHandler);
    }

    private static void summonParticlesPacketHandler(MinecraftClient client, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender s) {
        ParticleHolder holder = ParticleHolder.fromBuf(buf);
        client.execute(() -> {
            ParticleHelper.summonParticles(holder.particle(), client.world, new Vec3d(holder.x(), holder.y(), holder.z()), holder.radius(), holder.height());
        });
    }

    private static void recallParticlesPacketHandler(MinecraftClient client, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender s) {
        ParticleHolder holder = ParticleHolder.fromBuf(buf);
        client.execute(() -> {
            ParticleHelper.recallParticles(holder.particle(), client.world, new Vec3d(holder.x(), holder.y(), holder.z()), holder.radius(), holder.height());
        });
    }
}
