package dev.brodino.summonmounts.mount;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.flutes.FluteItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.SERVER)
public class Mount implements ParticleHelper {

    private final PlayerEntity summoner;
    private final AbstractHorseEntity entity;
    private final ItemStack stack;
    private final Identifier id;

    private Mount(PlayerEntity summoner, AbstractHorseEntity entity, ItemStack stack) {
        this.summoner = summoner;
        this.entity = entity;
        this.stack = stack;
        this.id = Registry.ENTITY_TYPE.getId(this.entity.getType());
    }

    public static void fromEntity(PlayerEntity summoner, AbstractHorseEntity entity, ItemStack stack) {
        Mount mount = new Mount(summoner, entity, stack);
        mount.recall();
    }

    public static Optional<Mount> fromStack(PlayerEntity player, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(SummonMounts.MOD_ID)) {
            return Optional.empty();
        }
        NbtCompound nbt = nbtCompound.getCompound(SummonMounts.MOD_ID);

        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("type")));
        Entity entity = entityType.create(player.getWorld());
        if (!(entity instanceof AbstractHorseEntity abstractHorseEntity)) {
            return Optional.empty();
        }

        applyNbtToEntity(nbt, abstractHorseEntity);

        return Optional.of(new Mount(player, abstractHorseEntity, stack));
    }

    public void recall() {
        FluteItem.saveMount(this.stack, this);
        // Save data inside itemstack
        this.entity.discard();
        this.recallParticles((ServerWorld) this.entity.getWorld(), this);

    }

    public void summon() {
        // Create the entity
        this.entity.setPosition(this.summoner.getPos());
        this.summoner.getWorld().spawnEntity(this.entity);
        this.summonParticles((ServerWorld) this.entity.getWorld(), this);
    }

    // Particle Helper Getters
    public Vec3d getPos() { return this.entity.getPos(); }
    public double getHeight() { return this.entity.getHeight(); }
    public double getRadius() { return this.entity.getBoundingBox().getAverageSideLength(); }

    public NbtCompound getSavableNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        entity.writeNbt(mountNbt);

        if (!entity.isDead()) {
            if (mountNbt.contains("ArmorItems")) { nbt.put("armor", mountNbt.get("ArmorItems")); }
            if (mountNbt.contains("SaddleItem")) { nbt.put("saddle", mountNbt.get("SaddleItem")); }
            if (mountNbt.contains("DecorItem")) { nbt.put("decor", mountNbt.get("DecorItem")); }
        }

        Text name = entity.getDisplayName();
        if (name != null) { nbt.putString("name", name.getString()); }

        UUID owner = entity.getOwnerUuid();
        if (owner != null) { nbt.putUuid("owner", owner); }

        if (mountNbt.contains("Variant")) { nbt.putInt("variant", mountNbt.getInt("Variant")); }

        nbt.putString("type", this.id.toString());

        return nbt;
    }

    private static void applyNbtToEntity(NbtCompound nbt, AbstractHorseEntity entity) {
        NbtCompound mountNbt = new NbtCompound();
        entity.writeNbt(mountNbt);

        if (nbt.contains("armor")) { mountNbt.put("ArmorItems", nbt.get("armor")); }
        if (nbt.contains("saddle")) { mountNbt.put("SaddleItem", nbt.get("saddle")); }
        if (nbt.contains("decor")) { mountNbt.put("DecorItem", nbt.get("decor")); }
        if (nbt.contains("variant")) { mountNbt.putInt("Variant", nbt.getInt("variant")); };
        if (nbt.contains("name")) { entity.setCustomName(Text.literal(nbt.getString("name"))); }
        if (nbt.contains("owner")) { entity.setOwnerUuid(nbt.getUuid("owner")); }
    }
}
