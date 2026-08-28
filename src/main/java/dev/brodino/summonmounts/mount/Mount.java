package dev.brodino.summonmounts.mount;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.flutes.FluteItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

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
        stack.addEnchantment(Enchantments.LOYALTY, 1);
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
        this.entity.discard();
        this.recallParticles((ServerWorld) this.entity.getWorld(), this);
    }

    private void dropInventory() {
        NbtCompound mountNbt = new NbtCompound();
        this.entity.writeNbt(mountNbt);

        if (mountNbt.contains("SaddleItem")) {
            ItemStack saddle = ItemStack.fromNbt(mountNbt.getCompound("SaddleItem"));
            if (!saddle.isEmpty()) this.entity.dropStack(saddle);
            mountNbt.remove("SaddleItem");
        }

        if (mountNbt.contains("ArmorItem")) {
            ItemStack armor = ItemStack.fromNbt(mountNbt.getCompound("ArmorItem"));
            if (!armor.isEmpty()) this.entity.dropStack(armor);
            mountNbt.remove("ArmorItem");
        }

        if (mountNbt.contains("DecorItem")) {
            ItemStack decor = ItemStack.fromNbt(mountNbt.getCompound("DecorItem"));
            if (!decor.isEmpty()) this.entity.dropStack(decor);
            mountNbt.remove("DecorItem");
        }
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

    public ItemStack getStack() { return this.stack; }

    public NbtCompound getSavableNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        this.entity.writeNbt(mountNbt);

        if (entity.isDead()) {
            this.dropInventory();
        } else {
            if (mountNbt.contains("ArmorItem")) { nbt.put("ArmorItem", mountNbt.get("ArmorItem")); }
            if (mountNbt.contains("SaddleItem")) { nbt.put("SaddleItem", mountNbt.get("SaddleItem")); }
            if (mountNbt.contains("DecorItem")) { nbt.put("DecorItem", mountNbt.get("DecorItem")); }
        }

        if (mountNbt.contains("CustomName")) { nbt.putString("CustomName", mountNbt.getString("CustomName")); }
        if (mountNbt.contains("Owner")) { nbt.putUuid("Owner", mountNbt.getUuid("Owner")); }
        if (mountNbt.contains("Variant")) { nbt.putInt("Variant", mountNbt.getInt("Variant")); }
        if (mountNbt.contains("Tame")) { nbt.putBoolean("Tame", mountNbt.getBoolean("Tame")); }

        nbt.putString("type", this.id.toString());

        return nbt;
    }

    private static void applyNbtToEntity(NbtCompound stackNbt, AbstractHorseEntity entity) {
        NbtCompound mountNbt = new NbtCompound();
        entity.writeNbt(mountNbt);

        if (stackNbt.contains("ArmorItem")) mountNbt.put("ArmorItem", stackNbt.get("ArmorItem"));
        if (stackNbt.contains("SaddleItem")) mountNbt.put("SaddleItem", stackNbt.get("SaddleItem"));
        if (stackNbt.contains("DecorItem")) mountNbt.put("DecorItem", stackNbt.get("DecorItem"));
        if (stackNbt.contains("CustomName")) mountNbt.putString("CustomName", stackNbt.getString("CustomName"));
        if (stackNbt.contains("Variant")) mountNbt.putInt("Variant", stackNbt.getInt("Variant"));
        if (stackNbt.contains("Tame")) mountNbt.putBoolean("Tame", stackNbt.getBoolean("Tame"));
        if (stackNbt.contains("Owner")) mountNbt.putUuid("Owner", stackNbt.getUuid("Owner"));

        entity.readNbt(mountNbt);
    }
}
