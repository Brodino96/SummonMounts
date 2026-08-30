package dev.brodino.summonmounts.network;

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
import net.minecraft.util.math.Vec3d;

public class NetworkManager {

    public static void sendParticlePacket(Packets packet, ServerPlayerEntity player, ParticleType<?> particle, Mount mount) {
        for (final ServerPlayerEntity target : Utils.getNearbyPlayers(player, mount.getPos())) {
            ServerPlayNetworking.send(target, packet.getIdentifier(), ParticleHolder.fromMount(particle, mount, packet.getIdentifier()).getBuf());
        }
    }

    public static void registerClientPacketReceivers() {
        for (Packets packet : Packets.values()) {
            ClientPlayNetworking.registerGlobalReceiver(packet.getIdentifier(), NetworkManager::handleParticlePacket);
        }
    }

    private static void handleParticlePacket(MinecraftClient client, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender s) {
        ParticleHolder holder = ParticleHolder.fromBuf(buf);
        client.execute(() -> {
            ParticleHelper.requestParticles(holder.packet(), holder.particle(), client.world, new Vec3d(holder.x(), holder.y(), holder.z()), holder.radius(), holder.height());
        });
    }
}
