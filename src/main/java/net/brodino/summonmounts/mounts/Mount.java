package net.brodino.summonmounts.mounts;

import net.brodino.summonmounts.ParticleHelper;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Mount {

    private final UUID uuid;
    private final UUID ownerUuid;
    private final EntityType<?> entityType;

    private NbtElement armorData;
    private NbtElement saddleData;
    private Text customName;
    private NbtCompound genericData;

    private AbstractHorseEntity activeEntity;
    private ItemStack boundItem;
    private int idleTicks;

    private Mount(UUID uuid, UUID ownerUuid, EntityType<?> entityType) {
        this.uuid = uuid;
        this.ownerUuid = ownerUuid;
        this.entityType = entityType;
        this.idleTicks = 0;
    }

    /**
     * Creates a mount from an existing entity (used when binding)
     * @param entity The horse entity to create a mount from
     * @return A new Mount instance with data from the provided entity
     */
    public Mount fromEntity(AbstractHorseEntity entity) {
        Mount mount = new Mount(
                entity.getUuid(),
                entity.getOwnerUuid(),
                entity.getType()
        );

        NbtCompound entityNbt = new NbtCompound();
        entity.writeNbt(entityNbt);

        if (entityNbt.contains("ArmorItems")) {
            mount.armorData = entityNbt.get("ArmorItems");
            entityNbt.remove("ArmorItems");
        }

        if (entityNbt.contains("SaddleItem")) {
            mount.saddleData = entityNbt.get("SaddleItem");
            entityNbt.remove("SaddleItem");
        }

        if (entity.getCustomName() != null) {
            mount.customName = entity.getCustomName();
        }

        mount.genericData = entityNbt;
        return mount;
    }

    /**
     * Creates a mount from an existing stack (used when summoning)
     * @param stack The stack containing the mount data
     * @return Optional containing the Mount if valid or empty
     */
    public static Optional<Mount> fromItemStack(ItemStack stack) {
        if (stack == null || !stack.hasNbt()) {
            return Optional.empty();
        }

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("mount.type") || !nbt.contains("mount.uuid")) {
            return Optional.empty();
        }

        String typeId = nbt.getString("mount.type");
        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(typeId));

        UUID mountUuid = nbt.getUuid("mount.uuid");
        UUID ownerUuid = nbt.contains("mount.owner") ? nbt.getUuid("mount.owner") : null;

        if (ownerUuid == null) {
            return Optional.empty();
        }

        Mount mount = new Mount(mountUuid, ownerUuid, entityType);

        if (nbt.contains("mount.genericData")) {
            mount.genericData = nbt.getCompound("mount.genericData").copy();
        }

        if (nbt.contains("mount.armor")) {
            mount.armorData = nbt.get("mount.armor");
        }

        if (nbt.contains("mount.saddle")) {
            mount.saddleData = nbt.get("mount.saddle");
        }

        if (nbt.contains("mount.name")) {
            mount.customName = Text.of(nbt.getString("mount.name"));
        }

        mount.boundItem = stack;

        return Optional.of(mount);
    }

    /**
     * Checks if this mount's entity type is allowed
     * @return true if the mount type is on the config
     */
    public boolean isAllowedType() {
        ArrayList<String> allowedMounts = SummonMounts.CONFIG.getAllowedMounts();
        if (allowedMounts.isEmpty()) {
            return true;
        }

        String typeId = Registry.ENTITY_TYPE.getId(entityType).toString();
        return allowedMounts.contains(typeId);
    }

    public boolean isInAllowedDimension(World world) {
        ArrayList<String> allowedDimensions = SummonMounts.CONFIG.getAllowedDimensions();
        if (allowedDimensions.isEmpty()) {
            return true;
        }

        return allowedDimensions.contains(world.getRegistryKey().getValue().toString());

    }

    /**
     * Summons the mount in the world
     * @param player The player using the item
     * @return Optional containing the mount entity or nothing
     */
    public Optional<AbstractHorseEntity> summon(ServerPlayerEntity player) {
        World currentWorld = player.getWorld();
        if (!this.isInAllowedDimension(currentWorld)) {
            return Optional.empty();
        }

        if (!this.isAllowedType()) {
            return Optional.empty();
        }

        Entity mount = this.entityType.create(currentWorld);
        if (mount == null) {
            return Optional.empty();
        }

        mount.setPosition(player.getPos());
        mount.setVelocity(0,0,0);
        mount.fallDistance = 0;

        currentWorld.spawnEntity(mount);

        if (SummonMounts.CONFIG.getParticlesOnSummon()) {
            ParticleHelper.summonParticles(mount);
        }

        player.startRiding(mount, true);

        return Optional.of((AbstractHorseEntity) mount);
    }









}
