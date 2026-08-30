package dev.brodino.summonmounts.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ParticleHelper {

    public static void recallParticles(Identifier particleId, ClientWorld world, Vec3d pos, double radius, double height) {
        ParticleEffect particleEffect = (ParticleEffect) Registry.PARTICLE_TYPE.get(particleId);
        if (particleEffect == null) return;
        drawSpiralParticle(pos, radius, height, 2, 20, world, particleEffect);
        drawCircleParticle(pos, radius, world, particleEffect);
        spawnParticlePlatform(pos, radius, 30, 0.3, world, particleEffect);
    }

    public static void summonParticles(Identifier particleId, ClientWorld world, Vec3d pos, double radius, double height) {
        ParticleEffect particleEffect = (ParticleEffect) Registry.PARTICLE_TYPE.get(particleId);
        if (particleEffect == null) return;
        drawConicalSpiralParticle(pos, radius, height, 2, 20, world, particleEffect);
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
