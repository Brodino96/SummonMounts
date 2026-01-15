package net.brodino.summonmounts;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleHelper {

    /**
     * @param center The center of the spiral.
     * @param radius The radius of the spiral.
     * @param height The height increment for each loop of the spiral.
     * @param turns The number of turns in the spiral.
     * @param pointsPerTurn The number of points per turn.
     */
    public static void drawSpiralParticle(Vec3d center, double radius, double height, int turns, int pointsPerTurn, World world, ParticleEffect particle) {
        if (world instanceof ServerWorld serverWorld){

            int totalPoints = turns * pointsPerTurn;
            for (int i = 0; i < totalPoints; i++) {
                double angle = i * (2 * Math.PI / pointsPerTurn);
                double x = center.x + radius * Math.cos(angle);
                double y = center.y + (i * height / pointsPerTurn); // Increment height
                double z = center.z + radius * Math.sin(angle);
                serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
            }

        }
    }

        public static void drawCircleParticle(Vec3d center, double radius, World world, ParticleEffect particle) {
            if (world instanceof ServerWorld serverWorld) {

                int points = (int) (radius * 20); // resolution: 20 points per block of radius
                double angleStep = 2 * Math.PI / points;
                for (int i = 0; i < points; i++) {
                    double angle = i * angleStep;
                    double x = center.x + radius * Math.cos(angle);
                    double y = center.y;
                    double z = center.z + radius * Math.sin(angle);
                    serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
                }

            }
        }


    /**
     * @param center The center of the spiral.
     * @param initialRadius The initial radius of the spiral at the base.
     * @param height The height increment for each loop of the spiral.
     * @param turns The number of turns in the spiral.
     * @param pointsPerTurn The number of points per turn.
     */
    public static void drawConicalSpiralParticle(Vec3d center, double initialRadius, double height, int turns, int pointsPerTurn, World world, ParticleEffect particle) {
        if (world instanceof ServerWorld serverWorld) {

            int totalPoints = turns * pointsPerTurn;
            for (int i = 0; i < totalPoints; i++) {
                double angle = i * (2 * Math.PI / pointsPerTurn);
                double currentHeight = i * height / pointsPerTurn; // Increment height
                double radius = initialRadius * (1 - (currentHeight / (height * turns)));
                if (radius < 0) radius = 0;

                double x = center.x + radius * Math.cos(angle);
                double y = center.y + currentHeight; // Height increasing
                double z = center.z + radius * Math.sin(angle);

                serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
            }

        }
    }

    public static void spawnParticlePlatform(Vec3d pos, double radius, int count,double speed,  World world,ParticleEffect particle){
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(particle, pos.x, pos.y, pos.z, count, radius,0,radius,speed);
        }
    }
}
