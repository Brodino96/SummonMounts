package dev.brodino.summonmounts.mount;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface ParticleHelper {

    default void recallParticles(ServerWorld world, Mount mount) {
        this.drawSpiralParticle(mount.getPos(), mount.getRadius(), mount.getHeight(), 2, 20, world, ParticleTypes.WITCH);
        this.drawCircleParticle(mount.getPos(), mount.getRadius(), world, ParticleTypes.DRAGON_BREATH);
        this.spawnParticlePlatform(mount.getPos(), mount.getRadius(), 30, 0.3, world, ParticleTypes.PORTAL);
    }

    default void summonParticles(ServerWorld world, Mount mount) {
        this.drawConicalSpiralParticle(mount.getPos(), mount.getRadius(), mount.getHeight(), 2, 20, world, ParticleTypes.WITCH);
    }

    private void drawSpiralParticle(Vec3d center, double radius, double height, int turns, int pointsPerTurn, ServerWorld world, ParticleEffect particle) {
        int totalPoints = turns * pointsPerTurn;
        for (int i = 0; i < totalPoints; i++) {
            double angle = i * (2 * Math.PI / pointsPerTurn);
            double x = center.x + radius * Math.cos(angle);
            double y = center.y + (i * height / pointsPerTurn); // Increment height
            double z = center.z + radius * Math.sin(angle);
            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private void drawCircleParticle(Vec3d center, double radius, ServerWorld world, ParticleEffect particle) {
        int points = (int) (radius * 20);
        double angleStep = 2 * Math.PI / points;
        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double x = center.x + radius * Math.cos(angle);
            double y = center.y;
            double z = center.z + radius * Math.sin(angle);
            world.spawnParticles(particle, x, y, z, 1,0,0,0,0);
        }
    }

    private void spawnParticlePlatform(Vec3d pos, double radius, int count, double speed, ServerWorld world, ParticleEffect particle) {
        world.spawnParticles(particle, pos.x, pos.y, pos.z, count, radius, 0, radius, speed);
    }

    private void drawConicalSpiralParticle(Vec3d center, double initialRadius, double height, int turns, int pointsPerTurn, ServerWorld world, ParticleEffect particle) {
        int totalPoints = turns * pointsPerTurn;
        for (int i = 0; i < totalPoints; i++) {
            double angle = i * (2 * Math.PI / pointsPerTurn);
            double currentHeight = i * height / pointsPerTurn; // Increment height
            double radius = initialRadius * (1 - (currentHeight / (height * turns)));
            if (radius < 0) radius = 0;

            double x = center.x + radius * Math.cos(angle);
            double y = center.y + currentHeight; // Height increasing
            double z = center.z + radius * Math.sin(angle);

            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
