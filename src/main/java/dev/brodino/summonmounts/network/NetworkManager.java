package dev.brodino.summonmounts.network;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.Utils;
import dev.brodino.summonmounts.client.ParticleHelper;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class NetworkManager {

    public static final Identifier FORCE_LAND = new Identifier(SummonMounts.MOD_ID, "force_land");

    public static void sendParticlePacket(Packets packet, ServerPlayerEntity player, ParticleType<?> particle, Mount mount) {
        for (final ServerPlayerEntity target : Utils.getNearbyPlayers(player, mount.getPos())) {
            ServerPlayNetworking.send(target, packet.getIdentifier(), ParticleHolder.fromMount(particle, mount, packet.getIdentifier()).getBuf());
        }
    }

    public static void sendForceLandPacket(ServerPlayerEntity player, UUID entityUuid, boolean forceLanding) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(entityUuid);
        buf.writeBoolean(forceLanding);
        ServerPlayNetworking.send(player, FORCE_LAND, buf);
    }

    public static void registerClientPacketReceivers() {
        for (Packets packet : Packets.values()) {
            ClientPlayNetworking.registerGlobalReceiver(packet.getIdentifier(), NetworkManager::handleParticlePacket);
        }
        ClientPlayNetworking.registerGlobalReceiver(FORCE_LAND, NetworkManager::handleForceLandPacket);
    }

    private static void handleParticlePacket(MinecraftClient client, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender s) {
        ParticleHolder holder = ParticleHolder.fromBuf(buf);
        client.execute(() -> {
            ParticleHelper.requestParticles(holder.packet(), holder.particle(), client.world, new Vec3d(holder.x(), holder.y(), holder.z()), holder.radius(), holder.height());
        });
    }

    private static void handleForceLandPacket(MinecraftClient client, ClientPlayNetworkHandler h, PacketByteBuf buf, PacketSender s) {
        UUID entityUuid = buf.readUuid();
        boolean forceLanding = buf.readBoolean();
        client.execute(() -> ForceLandTracker.setForceLanding(entityUuid, forceLanding));
    }
}
