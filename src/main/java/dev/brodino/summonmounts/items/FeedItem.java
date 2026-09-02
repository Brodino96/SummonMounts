package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.ParticleRegistry;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.Optional;

public class FeedItem extends SummonMountsItem {

    public static final Settings BASE_SETTINGS = new Settings()
            .group(ItemGroup.FOOD)
            .maxCount(64);

    public FeedItem(Settings settings, OcarinaTypes type) { super(settings, type); }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.world.isClient) {
            return ActionResult.PASS;
        }

        final String playerName = player.getName().getString();
        SummonMounts.LOGGER.info("{} is trying to use a feed item", playerName);

        if (!(entity instanceof AbstractHorseEntity horseEntity)) {
            SummonMounts.LOGGER.info("{} try failed because entity isn't allowed", playerName);
            return super.useOnEntity(stack, player, entity, hand);
        }

        Optional<Mount> mountOptional = MountManager.getMountFromEntity(horseEntity);
        if (mountOptional.isEmpty()) {
            SummonMounts.LOGGER.info("{} try failed because entity is not a mount", playerName);
            return ActionResult.PASS;
        }

        Mount mount = mountOptional.get();
        FeedItem feedItem = (FeedItem) stack.getItem();

        if (!feedItem.getType().equals(mount.getItem().getType())) {
            SummonMounts.LOGGER.info("{} try failed because feed item is not same tier as mount", playerName);
            return ActionResult.PASS;
        }

        Float repair = SummonMounts.CONFIG.getFoodRepair(feedItem.getType());
        if (repair == null) {
            SummonMounts.LOGGER.error("{} try failed because is missing config amount, item: {}", playerName, feedItem.getName().getString());
            return ActionResult.PASS;
        }

        if (!mountOptional.get().feedMount(repair)) {
            SummonMounts.LOGGER.info("{} try failed", playerName);
            return ActionResult.PASS;
        }

        stack.decrement(1);
        horseEntity.setPitch(30f);
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            int entityWidth = (int) (entity.getWidth());
            serverWorld.spawnParticles(ParticleRegistry.FEED_PARTICLE, entity.getX(), entity.getY() + 0.5, entity.getZ(), (int) (entityWidth * 10), entityWidth * 0.5, entity.getHeight() * 0.5,  entityWidth * 0.5, 0.5);
            entity.playSound(SoundEvents.ENTITY_HORSE_EAT, 1,1);
        }
        return ActionResult.CONSUME;
    }
}
