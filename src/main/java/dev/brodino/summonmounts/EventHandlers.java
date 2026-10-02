package dev.brodino.summonmounts;

import dev.brodino.summonmounts.commands.CommandHandler;
import dev.brodino.summonmounts.items.FeedItem;
import dev.brodino.summonmounts.mount.Mount;
import dev.brodino.summonmounts.particle.ParticlesManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.Optional;

public class EventHandlers {

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            MountManager.tick();
            Utils.tickTasks();
        });

        ServerPlayConnectionEvents.DISCONNECT.register(MountManager::onPlayerDisconnect);

        ServerLivingEntityEvents.ALLOW_DEATH.register(MountManager::onMountDeath);

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(MountManager::onDimensionChange);

        CommandRegistrationCallback.EVENT.register(CommandHandler::initialize);

        UseEntityCallback.EVENT.register(EventHandlers::onUseEntity);

        ServerLifecycleEvents.SERVER_STOPPING.register(MountManager::recallAllMounts);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((ms, r, s) -> {
            SummonMounts.CONFIG.reload();
        });
    }

    private static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (player.world.isClient || hitResult != null) return ActionResult.PASS; // Change to success or consume for future releases
        SummonMounts.LOGGER.info("Source: Event");
        SummonMounts.LOGGER.info("Hand: {}", hand);

        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof FeedItem feedItem)) return ActionResult.PASS;

        final String playerName = player.getName().getString();
        SummonMounts.LOGGER.info("{} is trying to use a feed item", playerName);

        if (!(entity instanceof AbstractHorseEntity horseEntity)) {
            SummonMounts.LOGGER.info("{} try failed because entity isn't allowed", playerName);
            return ActionResult.PASS;
        }

        Optional<Mount> mountOptional = MountManager.getMountFromEntity(horseEntity);
        if (mountOptional.isEmpty()) {
            SummonMounts.LOGGER.info("{} try failed because entity is not a mount", playerName);
            return ActionResult.PASS;
        }

        Mount mount = mountOptional.get();

        if (!feedItem.getType().equals(mount.getItem().getType())) {
            SummonMounts.LOGGER.info("{} try failed because feed item is not same tier as mount", playerName);
            return ActionResult.PASS;
        }

        Float repair = SummonMounts.CONFIG.getFoodRepair(feedItem.getType());
        if (repair == null) {
            SummonMounts.LOGGER.error("{} try failed because is missing config amount, item: {}", playerName, feedItem.getName().getString());
            return ActionResult.PASS;
        }

        if (!mount.feedMount(repair)) {
            SummonMounts.LOGGER.info("{} try failed", playerName);
            return ActionResult.PASS;
        }

        stack.decrement(1);
        horseEntity.setPitch(30f);
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            int entityWidth = (int) (entity.getWidth());
            serverWorld.spawnParticles(ParticlesManager.FEED_PARTICLE, entity.getX(), entity.getY() + 0.5, entity.getZ(), (int) (entityWidth * 10), entityWidth * 0.5, entity.getHeight() * 0.5,  entityWidth * 0.5, 0.5);
            entity.playSound(SoundEvents.ENTITY_HORSE_EAT, 1,1);
        }
        return ActionResult.CONSUME;
    }

}
