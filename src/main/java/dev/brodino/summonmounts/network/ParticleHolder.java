package dev.brodino.summonmounts.network;

import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record ParticleHolder(int color, Identifier packet, double x, double y, double z, int id) {

    public static ParticleHolder fromBuf(PacketByteBuf buf) {
        return new ParticleHolder(buf.readInt(), buf.readIdentifier(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt());
    }

    public static ParticleHolder fromMount(int color, Mount mount, Identifier packet) {
        final Vec3d pos = mount.getPos();
        return new ParticleHolder(color, packet, pos.x, pos.y, pos.z, mount.getId());
    }

    public PacketByteBuf getBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.color);
        buf.writeIdentifier(this.packet);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.id);
        return buf;
    }

}
