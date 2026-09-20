package dev.brodino.summonmounts.mount;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface PositionHelper {
    
    default void positionMount(AbstractHorseEntity entity, PlayerEntity player) {
        entity.setPosition(this.findSafeSpawnPosition(player, entity, player.getWorld()));
        entity.setYaw(player.headYaw);
        entity.setHeadYaw(player.headYaw);
        entity.setBodyYaw(player.headYaw);
    }

    private Vec3d findSafeSpawnPosition(PlayerEntity player, Entity mount, World world) {
        double mountWidth = mount.getWidth();
        double mountHeight = mount.getHeight();

        // Candidate offsets relative to player: at player, in front, left, right, behind
        float yaw = player.getYaw();
        double radYaw = Math.toRadians(yaw);
        double fx = -Math.sin(radYaw); // forward X
        double fz =  Math.cos(radYaw); // forward Z

        double[][] offsets = {
                {0, 0},                 // at player
                {fx, fz},               // in front
                {-fz, fx},              // left
                {fz, -fx},              // right
                {-fx, -fz},             // behind
        };

        for (double[] offset : offsets) {
            double cx = player.getX() + offset[0] * (mountWidth + 0.5);
            double cz = player.getZ() + offset[1] * (mountWidth + 0.5);

            // Try the player's Y and up to 3 blocks above
            for (int dy = 0; dy <= 3; dy++) {
                double cy = player.getY() + dy;
                if (this.isClearForMount(world, cx, cy, cz, mountWidth, mountHeight)) {
                    return new Vec3d(cx, cy, cz);
                }
            }
        }

        // Fallback: player's position
        return player.getPos();
    }

    private boolean isClearForMount(World world, double x, double y, double z, double width, double height) {
        double half = width / 2.0;
        Box box = new Box(x - half, y, z - half, x + half, y + height, z + half);
        return world.isSpaceEmpty(box);
    }
}
