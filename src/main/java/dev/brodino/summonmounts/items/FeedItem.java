package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import dev.brodino.summonmounts.particle.ParticlesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.Optional;

public class FeedItem extends SummonMountsItem {

    public static final Settings BASE_SETTINGS = new Settings()
            .group(ItemGroup.FOOD)
            .maxCount(64);

    public FeedItem(Settings settings, OcarinaTypes type) { super(settings, type); }

    public static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (player.world.isClient || hitResult != null) return ActionResult.PASS; // Change to success or consume for future releases

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
