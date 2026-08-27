package dev.brodino.summonmounts.mount;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.SERVER)
public class Mount implements ParticleHelper {

    private final ServerPlayerEntity summoner;
    private final AbstractHorseEntity entity;
    private final ItemStack stack;

    private Mount(ServerPlayerEntity summoner, AbstractHorseEntity entity, ItemStack stack) {
        this.summoner = summoner;
        this.entity = entity;
        this.stack = stack;
    }

    public static Mount fromEntity(ServerPlayerEntity summoner, AbstractHorseEntity entity, ItemStack stack) {
        Mount mount = new Mount(summoner, entity, stack);
        mount.recall();
        return mount;
    }

    public void recall() {
        // Save data inside itemstack
        this.entity.discard();
        this.recallParticles((ServerWorld) this.entity.getWorld(), this);

    }

    public void summon() {
        // Create the entity
        this.summonParticles((ServerWorld) this.entity.getWorld(), this);
    }

    // Particle Helper Getters
    public Vec3d getPos() { return this.entity.getPos(); }
    public double getHeight() { return this.entity.getHeight(); }
    public double getRadius() { return this.entity.getBoundingBox().getAverageSideLength(); }
}
