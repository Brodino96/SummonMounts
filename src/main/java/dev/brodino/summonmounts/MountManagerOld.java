package dev.brodino.summonmounts;

import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import net.adventurez.init.SoundInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class MountManagerOld {
    private static final Map<UUID, UUID> playerMounts = new HashMap<>();
    private static final Map<UUID, ItemStack> playerItems = new HashMap<>();
    private static final Map<UUID, Integer> mountTimers = new HashMap<>();
    private static final Integer DESPAWN_TIMER = SummonMounts.OLD_CONFIG.getDespawnTime() * 20;

    /**
     * Method to associate a clean mount to an item
     * @param player The player using the item
     * @param entity The target entity
     * @param stack The ItemStack used
     * @return true if mount get linked
     */
    public static boolean bindMountToItem(PlayerEntity player, Entity entity, ItemStack stack) {

        String playerName = player.getDisplayName().getString();

        if (!(entity instanceof AbstractHorseEntity)) {
            return false;
        }

        UUID mountUuid = entity.getUuid();
        if (playerMounts.containsValue(mountUuid)) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().binding.alreadyBounded), true);
            return false;
        }

        AbstractHorseEntity mount = (AbstractHorseEntity) entity;

        if (!mount.isTame() || !player.getUuid().equals(mount.getOwnerUuid())) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().binding.notYours), true);
            return false;
        }

        String mountId = Registry.ENTITY_TYPE.getId(mount.getType()).toString();
        boolean isAllowed = false;
        for (String allowedType : SummonMounts.OLD_CONFIG.getAllowedSummons()) {
            if (allowedType.equals(mountId)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().binding.notAllowed), true);
            return false;
        }

        NBTHelper.saveMountData(mount, stack, false);
        NBTHelper.setCustomLore(stack, "Contains: " + mount.getDisplayName().getString());
        mount.discard();

        player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().binding.success), true);

        SummonMounts.LOGGER.info("{} successfully bound a mount to his item", playerName);

        return true;
    }

    /**
     * Finds a safe spawn position near the player that is not inside a solid block.
     * Tries the player's exact position first, then positions in front, beside, and behind.
     * Scans upward up to 3 blocks if the candidate column is obstructed.
     * Falls back to the player's position if no clear spot is found.
     */
    private static Vec3d findSafeSpawnPosition(PlayerEntity player, Entity mount, World world) {
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
                if (isClearForMount(world, cx, cy, cz, mountWidth, mountHeight)) {
                    return new Vec3d(cx, cy, cz);
                }
            }
        }

        // Fallback: player's position
        return player.getPos();
    }

    /**
     * Returns true if the axis-aligned box for the mount at (x, y, z) does not
     * intersect any solid block collision shape.
     */
    private static boolean isClearForMount(World world, double x, double y, double z, double width, double height) {
        double half = width / 2.0;
        Box box = new Box(x - half, y, z - half, x + half, y + height, z + half);
        return world.isSpaceEmpty(box);
    }

    public static Entity summonMount(PlayerEntity player, ItemStack stack) {

        String playerName = player.getDisplayName().getString();

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("mount.type")) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().spawn.noSavedData), true);
            return null;
        }

        UUID playerUuid = player.getUuid();

        String mountTypeId = nbt.getString("mount.type");
        World world = SummonMounts.SERVER.getOverworld();
        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(mountTypeId));

        Entity entity = entityType.create(world);
        if (entity == null) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().spawn.spawnFailed), true);
            return null;
        }

        Entity mount = NBTHelper.loadMountData((AbstractHorseEntity) entity, nbt);

        // Find a safe spawn position that isn't inside a block
        Vec3d spawnPos = findSafeSpawnPosition(player, mount, world);

        mount.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
        mount.setYaw(player.getYaw());
        mount.setHeadYaw(player.getYaw());
        mount.setVelocity(0,0,0);
        mount.fallDistance = 0;

        ParticleHelper.drawConicalSpiralParticle(mount.getPos(),mount.getBoundingBox().getAverageSideLength(),mount.getHeight(), 2,20,player.world,ParticleTypes.WITCH);

        world.spawnEntity(mount);

        // Re-apply variant after spawning, since the spawn packet may override it
        NBTHelper.applyVariant((AbstractHorseEntity) mount, nbt);

        playerMounts.put(playerUuid, mount.getUuid());
        playerItems.put(playerUuid, stack);

        player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().spawn.success), true);
        SummonMounts.LOGGER.info("{} successfully summoned a mount", playerName);

        return mount;
    }

    public static ItemStack dismissMount(PlayerEntity player) {

        String playerName = player.getDisplayName().getString();

        UUID playerUUID = player.getUuid();

        if (!playerMounts.containsKey(playerUUID)) {
            return ItemStack.EMPTY;
        }

        UUID mountUUID = playerMounts.get(playerUUID);

        Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);
        if (mount == null || !mount.isAlive()) {
            return ItemStack.EMPTY;
        }

        ItemStack output = NBTHelper.saveMountData(mount, playerItems.get(playerUUID), false);
        NBTHelper.setCustomLore(output, "Contains: " + mount.getDisplayName().getString());

        //PARTICLE EFFECT
        Vec3d mountPos = mount.getPos();
        double mountHeight = mount.getHeight();
        double mountRadius = mount.getBoundingBox().getAverageSideLength();

        ParticleHelper.drawSpiralParticle(mountPos,mountRadius,mountHeight, 2,20,player.world,ParticleTypes.WITCH);
        ParticleHelper.drawCircleParticle(mountPos, mountRadius, player.world, ParticleTypes.DRAGON_BREATH);
        ParticleHelper.spawnParticlePlatform(mountPos, mountRadius, 30,0.3, player.world, ParticleTypes.PORTAL);
        //

        mount.discard();
        player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().dismiss.success), true);
        SummonMounts.LOGGER.info("Successfully dismissed {}'s mount", playerName);

        playerMounts.remove(playerUUID);
        mountTimers.remove(mountUUID);
        playerItems.remove(playerUUID);

        return output;
    }

    public static void tickMounts() {
        Map<UUID, UUID> mountsCopy = new HashMap<>(playerMounts);

        for (Map.Entry<UUID, UUID> entry : mountsCopy.entrySet()) {
            UUID playerUUID = entry.getKey();
            UUID mountUUID = entry.getValue();

            Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);
            if (mount == null || !mount.isAlive()) {
                playerMounts.remove(playerUUID);
                mountTimers.remove(mountUUID);
                continue;
            }

            if (mount.hasPassengers()) {
                mountTimers.remove(mountUUID);
            } else {
                Integer timer = mountTimers.getOrDefault(mountUUID, DESPAWN_TIMER);
                timer--;

                if (timer <= 0) {

                    ServerPlayerEntity owner = SummonMounts.SERVER.getPlayerManager().getPlayer(playerUUID);

                    if (owner != null) {
                        MountManagerOld.dismissMount(owner);
                    }

                } else {
                    mountTimers.put(mountUUID, timer);
                }
            }

        }
    }

    public static void playerDisconnected(ServerPlayerEntity player) {

        UUID playerUUID = player.getUuid();
        if (!playerMounts.containsKey(playerUUID)) {
            return;
        }

        UUID mountUUID = playerMounts.get(playerUUID);
        Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);

        if (mount != null && mount.isAlive()) {
            MountManagerOld.dismissMount(player);
        }
    }

    public static boolean hasActiveMount(UUID playerUUID, ItemStack stack) {
        if (!playerMounts.containsKey(playerUUID)) {
            return false;
        }

        UUID mountUUID = playerMounts.get(playerUUID);

        if (SummonMounts.SERVER == null) return false;

        Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);
        if (mount == null || !mount.isAlive()) {
            playerMounts.remove(playerUUID);
            playerItems.remove(playerUUID);
            mountTimers.remove(mountUUID);
            return false;
        }

        return true;
    }

    /**
     * Handles the death of a mount by intercepting it before it happens.
     * Saves the mount data and recalls the mount instead of letting it die.
     * @param entity The entity that is about to die
     * @return true to allow death (for non-tracked mounts), false to prevent death (for tracked mounts)
     */
    public static boolean onMountDeath(Entity entity) {
        if (!(entity instanceof AbstractHorseEntity mount)) {
            return true; // Allow death for non-horse entities
        }

        UUID ownerUUID = mount.getOwnerUuid();
        if (ownerUUID == null) {
            return true; // Allow death for untamed mounts
        }

        // Check if this mount is being tracked by our system
        UUID mountUUID = mount.getUuid();
        if (!playerMounts.containsValue(mountUUID)) {
            return true; // Allow death for mounts not tracked by our system
        }

        SummonMounts.LOGGER.info("Intercepting mount death, recalling mount instead");

        ServerPlayerEntity owner = SummonMounts.SERVER.getPlayerManager().getPlayer(ownerUUID);

        // Get the item associated with this mount
        ItemStack mountItem = playerItems.get(ownerUUID);
        if (mountItem == null || mountItem.isEmpty()) {
            SummonMounts.LOGGER.warn("Could not find mount item for player, allowing death");
            playerMounts.remove(ownerUUID);
            mountTimers.remove(mountUUID);
            playerItems.remove(ownerUUID);
            return true;
        }

        // Set health to max before saving so mount respawns at full health
        mount.setHealth(mount.getMaxHealth());

        // Save mount data BEFORE death (with dead=false to preserve armor, saddle, health)
        NBTHelper.saveMountData(mount, mountItem, false);
        NBTHelper.setCustomLore(mountItem, "Contains: " + mount.getDisplayName().getString());
        SummonMounts.LOGGER.info("Mount data saved successfully before death (health reset to max)");

        // Particle effects for recall
        Vec3d mountPos = mount.getPos();
        double mountHeight = mount.getHeight();
        double mountRadius = mount.getBoundingBox().getAverageSideLength();

        ParticleHelper.drawSpiralParticle(mountPos, mountRadius, mountHeight, 2, 20, owner.world, ParticleTypes.WITCH);
        ParticleHelper.drawCircleParticle(mountPos, mountRadius, owner.world, ParticleTypes.DRAGON_BREATH);
        ParticleHelper.spawnParticlePlatform(mountPos, mountRadius, 30, 0.3, owner.world, ParticleTypes.PORTAL);

        // Discard the mount (remove from world without dropping items)
        mount.discard();

        // Clean up the maps
        playerMounts.remove(ownerUUID);
        mountTimers.remove(mountUUID);
        playerItems.remove(ownerUUID);

        // Notify the player
        owner.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().dismiss.success), true);

        // Return false to prevent the actual death (which would drop items)
        return false;
    }

    /**
     * Handles item use on entities (binding mounts)
     *
     * @param player The player using the item
     * @param world  The world
     * @param hand   The hand used
     * @param target The entity being targeted
     */
    public static void itemUsedOnAnEntity(PlayerEntity player, World world, Hand hand, Entity target, EntityHitResult hitResult) {

        ItemStack stack = player.getStackInHand(hand);

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.OLD_CONFIG.getSummonItem()));

        if (stack.getItem() != summonItem) {
            return;
        }

        if (!stack.hasNbt() || !stack.getNbt().contains("mount.uuid")) {
            MountManagerOld.bindMountToItem(player, target, stack);
        }

    }

    public static ActionResult itemUsedOnABlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {

        ItemStack stack = player.getStackInHand(hand);

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.OLD_CONFIG.getSummonItem()));

        if (!stack.getItem().equals(summonItem)) {
            return ActionResult.PASS;
        }

        if (hasActiveMount(player.getUuid(), stack)) {
            player.sendMessage(Text.literal("Non puoi farlo"), true);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    /**
     * Handles item use (summoning or dismissing mounts)
     * @param player The player using the item
     * @param world The world
     * @param hand The hand used
     * @return The action result
     */
    public static TypedActionResult<ItemStack> onItemUse(PlayerEntity player, World world, Hand hand) {

        ItemStack stack = player.getStackInHand(hand);
        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.OLD_CONFIG.getSummonItem()));

        if (stack.getItem() != summonItem) {
            return TypedActionResult.pass(stack);
        }

        if (TagData.getCombat((IEntityDataSaver) player)) {
            player.sendMessage(Text.literal( SummonMounts.OLD_CONFIG.getLocales().itemUse.inCombat), true);
            return TypedActionResult.fail(stack);
        }

        if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return TypedActionResult.fail(stack);
        }

        player.getItemCooldownManager().set(stack.getItem(), SummonMounts.OLD_CONFIG.getItemCooldown() * 20);
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundInit.FLUTE_CALL_EVENT, SoundCategory.AMBIENT, 1f, 1f);
        if (!SummonMounts.OLD_CONFIG.getAllowedDimensions().contains(player.getWorld().getRegistryKey().getValue().toString())) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().itemUse.wrongDimension), true);
            return TypedActionResult.pass(stack);
        }

        if (!stack.hasNbt()) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().itemUse.notBounded), true);
        }

        NbtCompound nbt = stack.getNbt();
        if (!nbt.contains("mount.uuid")) {
            player.sendMessage(Text.literal(SummonMounts.OLD_CONFIG.getLocales().itemUse.notBounded), true);
            return TypedActionResult.pass(stack);
        }

        UUID playerUUID = player.getUuid();

        if (!MountManagerOld.hasActiveMount(playerUUID, stack)) {
            Entity mount = MountManagerOld.summonMount(player, stack);
            if (mount != null) {
                return TypedActionResult.pass(stack);
            }


        } else {
            if (!playerMounts.get(playerUUID).equals(nbt.getUuid("mount.uuid"))) {
                player.sendMessage(Text.of(SummonMounts.OLD_CONFIG.getLocales().itemUse.wrongItem), true);
                return TypedActionResult.pass(stack);
            }

            ItemStack out = MountManagerOld.dismissMount(player);
            if (!out.equals(ItemStack.EMPTY)) {
                player.setStackInHand(hand, out);
                return TypedActionResult.pass(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }

}
























