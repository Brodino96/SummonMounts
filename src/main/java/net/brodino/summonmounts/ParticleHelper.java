package net.brodino.summonmounts;

/* By ArgoSeven:
 * https://github.com/ArgoSeven
 * Merged in PR [4]:
 * https://github.com/Brodino96/SummonMounts/pull/4
 * Edited to better fit needs
 */

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ParticleHelper {

    /**
     * Draws particles around the specified entity
     */
    public static void summonParticles(AbstractHorseEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) {
            return;
        }

        int turns = 2;
        int pointsPerTurn = 20;
        float height = entity.getHeight();
        int totalPoints = turns * pointsPerTurn;

        for (int i = 0; i < totalPoints; i++) {
            double angle = i * (2 * Math.PI / pointsPerTurn);
            double currentHeight = i * height / pointsPerTurn; // Increment height
            double radius = entity.getBoundingBox().getAverageSideLength() * (1 - (currentHeight / (height * turns)));
            if (radius < 0) radius = 0;

            Vec3d center = entity.getPos();
            double x = center.x + radius * Math.cos(angle);
            double y = center.y + currentHeight; // Height increasing
            double z = center.z + radius * Math.sin(angle);

            world.spawnParticles(ParticleTypes.WITCH, x, y, z, 1, 0, 0, 0, 0);
        }
    }


    public static void dismissParticles(AbstractHorseEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) {
            return;
        }

        Vec3d pos = entity.getPos();
        double radius = entity.getBoundingBox().getAverageSideLength();
        float height = entity.getHeight();

        drawSpiralParticle(pos, radius, height, world);
        drawCircleParticle(pos, radius, world);
        spawnParticlePlatform(pos, radius, world);
    }

    private static void drawSpiralParticle(Vec3d center, double radius, double height, ServerWorld world) {
        int turns = 2;
        int pointsPerTurn = 20;
        int totalPoints = turns * pointsPerTurn;
        for (int i = 0; i < totalPoints; i++) {
            double angle = i * (2 * Math.PI / pointsPerTurn);
            double x = center.x + radius * Math.cos(angle);
            double y = center.y + (i * height / pointsPerTurn); // Increment height
            double z = center.z + radius * Math.sin(angle);
            world.spawnParticles((ParticleEffect) ParticleTypes.WITCH, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void drawCircleParticle(Vec3d center, double radius, ServerWorld world) {
        int points = (int) (radius * 20); // resolution: 20 points per block of radius
        double angleStep = 2 * Math.PI / points;
        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double x = center.x + radius * Math.cos(angle);
            double y = center.y;
            double z = center.z + radius * Math.sin(angle);
            world.spawnParticles((ParticleEffect) ParticleTypes.DRAGON_BREATH, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnParticlePlatform(Vec3d pos, double radius, ServerWorld world){
        world.spawnParticles((ParticleEffect) ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 30, radius,0, radius, 0.3);
    }
}
