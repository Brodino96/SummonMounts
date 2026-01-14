package net.brodino.summonmounts;

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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MountManager {
    private static final Map<UUID, UUID> playerMounts = new HashMap<>();
    private static final Map<UUID, ItemStack> playerItems = new HashMap<>();
    private static final Map<UUID, Integer> mountTimers = new HashMap<>();
    private static final Integer DESPAWN_TIMER = SummonMounts.CONFIG.despawnTime() * 20;

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
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().binding.alreadyBounded), true);
            return false;
        }

        AbstractHorseEntity mount = (AbstractHorseEntity) entity;

        if (!mount.isTame() || !player.getUuid().equals(mount.getOwnerUuid())) {
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().binding.notYours), true);
            return false;
        }

        String mountId = Registry.ENTITY_TYPE.getId(mount.getType()).toString();
        boolean isAllowed = false;
        for (String allowedType : SummonMounts.CONFIG.allowedSummons()) {
            if (allowedType.equals(mountId)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().binding.notAllowed), true);
            return false;
        }

        NBTHelper.saveMountData(mount, stack, false);
        NBTHelper.setCustomLore(stack, "Contains: " + mount.getDisplayName().getString());
        mount.discard();

        player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().binding.success), true);

        SummonMounts.LOGGER.info("{} successfully bound a mount to his item", playerName);

        return true;
    }

    public static Entity summonMount(PlayerEntity player, ItemStack stack) {

        String playerName = player.getDisplayName().getString();

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("mount.type")) {
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().spawn.noSavedData), true);
            return null;
        }

        UUID playerUuid = player.getUuid();

        String mountTypeId = nbt.getString("mount.type");
        World world = SummonMounts.SERVER.getOverworld();
        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(mountTypeId));

        Entity entity = entityType.create(world);
        if (entity == null) {
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().spawn.spawnFailed), true);
            return null;
        }

        Entity mount = NBTHelper.loadMountData((AbstractHorseEntity) entity, nbt);

        //PARTICLE EFFECT
        ParticleHelper.drawConicalSpiralParticle(mount.getPos(),mount.getBoundingBox().getAverageSideLength(),mount.getHeight(), 2,20,player.world,ParticleTypes.WITCH);
        //


        mount.setPosition(player.getX(), player.getY(), player.getZ());
        mount.setVelocity(0,0,0);
        mount.fallDistance = 0;
        world.spawnEntity(mount);

        playerMounts.put(playerUuid, mount.getUuid());
        playerItems.put(playerUuid, stack);

        player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().spawn.success), true);
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
        player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().dismiss.success), true);
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
                        MountManager.dismissMount(owner);
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
            MountManager.dismissMount(player);
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
     * Handles the death of a mount
     * @param entity The entity that died
     */
    public static void onMountDeath(Entity entity) {
        if (!(entity instanceof AbstractHorseEntity)) {
            return;
        }

        SummonMounts.LOGGER.info("A mount has died, beginning removal process");

        AbstractHorseEntity mount = (AbstractHorseEntity) entity;
        UUID ownerUUID = mount.getOwnerUuid();

        if (ownerUUID == null) {
            return;
        }

        ServerPlayerEntity owner = SummonMounts.SERVER.getPlayerManager().getPlayer(ownerUUID);
        if (owner == null) {
            return;
        }

        ItemStack stack = MountManager.playerItems.get(ownerUUID);
        NBTHelper.saveMountData(mount, stack, true);
        NBTHelper.setCustomLore(stack, "Contains: " + mount.getDisplayName().getString());

        // Clean up the maps
        playerMounts.remove(ownerUUID);
        mountTimers.remove(entity.getUuid());
        playerItems.remove(ownerUUID);
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

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

        if (stack.getItem() != summonItem) {
            return;
        }

        if (!stack.hasNbt() || !stack.getNbt().contains("mount.genericData")) {
            MountManager.bindMountToItem(player, target, stack);
        }

    }

    public static ActionResult itemUsedOnABlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {

        ItemStack stack = player.getStackInHand(hand);

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

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
        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

        if (stack.getItem() != summonItem) {
            return TypedActionResult.pass(stack);
        }

        if (TagData.getCombat((IEntityDataSaver) player)) {
            player.sendMessage(Text.literal( SummonMounts.CONFIG.locales().itemUse.inCombat), true);
            return TypedActionResult.fail(stack);
        }

        if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return TypedActionResult.fail(stack);
        }

        player.getItemCooldownManager().set(stack.getItem(), SummonMounts.CONFIG.itemCooldown() * 20);

        if (!SummonMounts.CONFIG.allowedDimensions().contains(player.getWorld().getRegistryKey().getValue().toString())) {
            player.sendMessage(Text.literal( SummonMounts.CONFIG.locales().itemUse.wrongDimension), true);
            return TypedActionResult.pass(stack);
        }

        if (!stack.hasNbt()) {
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().itemUse.notBounded), true);
        }

        NbtCompound nbt = stack.getNbt();
        if (!nbt.contains("mount.genericData") || !nbt.contains("mount.uuid")) {
            player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().itemUse.notBounded), true);
            return TypedActionResult.pass(stack);
        }

        UUID playerUUID = player.getUuid();

        if (!MountManager.hasActiveMount(playerUUID, stack)) {
            Entity mount = MountManager.summonMount(player, stack);
            if (mount != null) {
                return TypedActionResult.pass(stack);
            }


        } else {
            if (!playerMounts.get(playerUUID).equals(nbt.getUuid("mount.uuid"))) {
                player.sendMessage(Text.of(SummonMounts.CONFIG.locales().itemUse.wrongItem), true);
                return TypedActionResult.pass(stack);
            }

            ItemStack out = MountManager.dismissMount(player);
            if (!out.equals(ItemStack.EMPTY)) {
                player.setStackInHand(hand, out);
                return TypedActionResult.pass(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }

}
























