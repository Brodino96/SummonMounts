package dev.brodino.summonmounts.client;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.network.Packets;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class ParticleHelper {

    private static final Map<Identifier, ParticleAction> PACKET_HANDLERS = Map.of(
            Packets.SUMMON.getIdentifier(), ParticleHelper::summonParticles,
            Packets.RECALL.getIdentifier(), ParticleHelper::recallParticles,
            Packets.FEED.getIdentifier(), ParticleHelper::feedParticles
    );

    @FunctionalInterface
    private interface ParticleAction {
        void run(ParticleEffect particle, ClientWorld world, Vec3d pos, double radius, double height);
    }

    public static void requestParticles(Identifier packet, Identifier particle, ClientWorld world, Vec3d pos, double radius, double height) {
        ParticleEffect particleEffect = (ParticleEffect) Registry.PARTICLE_TYPE.get(particle);
        if (particleEffect == null) {
            SummonMounts.LOGGER.error("Failed to handle packet [{}] because particle was null", packet.toString());
            return;
        }
        ParticleAction action = PACKET_HANDLERS.get(packet);
        if (action == null) {
            SummonMounts.LOGGER.error("Failed to handle packet [{}] because action was null", packet.toString());
            return;
        }
        action.run(particleEffect, world, pos, radius, height);
    }


    public static void recallParticles(ParticleEffect particleEffect, ClientWorld world, Vec3d pos, double radius, double height) {
        drawSpiralParticle(pos, radius, height, 2, 20, world, particleEffect);
        drawCircleParticle(pos, radius, world, particleEffect);
        spawnParticlePlatform(pos, radius, 30, 0.3, world, particleEffect);
    }

    public static void summonParticles(ParticleEffect particleEffect, ClientWorld world, Vec3d pos, double radius, double height) {
        drawConicalSpiralParticle(pos, radius, height, 2, 20, world, particleEffect);
    }

    public static void feedParticles(ParticleEffect particleEffect, ClientWorld world, Vec3d pos, double radius, double height) {
        // @ArgoSeven do this
    }

    private static void drawSpiralParticle(Vec3d center, double radius, double height, int turns, int pointsPerTurn, ClientWorld world, ParticleEffect particle) {
        int totalPoints = turns * pointsPerTurn;
        for (int i = 0; i < totalPoints; i++) {
            double angle = i * (2 * Math.PI / pointsPerTurn);
            double x = center.x + radius * Math.cos(angle);
            double y = center.y + (i * height / pointsPerTurn); // Increment height
            double z = center.z + radius * Math.sin(angle);
            world.addParticle(particle,true, x, y, z, 0, 0, 0);
        }
    }

    private static void drawCircleParticle(Vec3d center, double radius, ClientWorld world, ParticleEffect particle) {
        int points = (int) (radius * 20);
        double angleStep = 2 * Math.PI / points;
        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double x = center.x + radius * Math.cos(angle);
            double y = center.y;
            double z = center.z + radius * Math.sin(angle);
            world.addParticle(particle,true, x, y, z, 0,0,0);
        }
    }

    private static void spawnParticlePlatform(Vec3d pos, double radius, int count, double speed, ClientWorld world, ParticleEffect particle) {
        for (int i = 0; i < count; i++) {
            double x = (world.random.nextGaussian() * radius) +  pos.x;
            double y = (world.random.nextGaussian()) +  pos.y;
            double z = (world.random.nextGaussian() * radius) +  pos.z;

            double xVel = world.random.nextGaussian() * speed;
            double yVel = world.random.nextGaussian() * speed;
            double zVel = world.random.nextGaussian() * speed;

            world.addParticle(particle, x, y, z, xVel, yVel, zVel);
        }
    }

    private static void drawConicalSpiralParticle(Vec3d center, double initialRadius, double height, int turns, int pointsPerTurn, ClientWorld world, ParticleEffect particle) {
        int totalPoints = turns * pointsPerTurn;
        for (int i = 0; i < totalPoints; i++) {
            double angle = i * (2 * Math.PI / pointsPerTurn);
            double currentHeight = i * height / pointsPerTurn; // Increment height
            double radius = initialRadius * (1 - (currentHeight / (height * turns)));
            if (radius < 0) radius = 0;

            double x = center.x + radius * Math.cos(angle);
            double y = center.y + currentHeight; // Height increasing
            double z = center.z + radius * Math.sin(angle);

            world.addParticle(particle,true, x, y, z, 0, 0, 0);
        }
    }
}
