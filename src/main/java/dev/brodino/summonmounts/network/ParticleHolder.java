package dev.brodino.summonmounts.network;

import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public record ParticleHolder(Identifier particle, Identifier packet, double x, double y, double z, double radius, double height) {

    public static ParticleHolder fromBuf(PacketByteBuf buf) {
        return new ParticleHolder(buf.readIdentifier(), buf.readIdentifier(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static ParticleHolder fromMount(ParticleType<?> particle, Mount mount, Identifier packet) {
        final Vec3d pos = mount.getPos();
        return new ParticleHolder(Registry.PARTICLE_TYPE.getId(particle), packet, pos.x, pos.y, pos.z, mount.getRadius(), mount.getHeight());
    }

    public PacketByteBuf getBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(this.particle);
        buf.writeIdentifier(this.packet);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.radius);
        buf.writeDouble(this.height);
        return buf;
    }

}
