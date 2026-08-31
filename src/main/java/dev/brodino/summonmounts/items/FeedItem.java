package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

        if (!(entity instanceof AbstractHorseEntity horseEntity)) {
            return super.useOnEntity(stack, player, entity, hand);
        }

        if (stack.isEmpty()) {
            return ActionResult.PASS;
        }

        Optional<Mount> mountOptional = MountManager.getMountFromEntity(horseEntity);
        if (mountOptional.isEmpty()) {
            SummonMounts.LOGGER.info("Mount is null");
            return ActionResult.PASS;
        }
        Mount mount = mountOptional.get();

        if (!(stack.getItem() instanceof FeedItem feedItem)) {
            SummonMounts.LOGGER.info("Item is not a ration item");
            return ActionResult.PASS;
        }

        if (!feedItem.getType().equals(mount.getItem().getType())) {
            SummonMounts.LOGGER.info("Ration item is not same tier as ocarina");
            return ActionResult.PASS;
        }

        Float repair = SummonMounts.CONFIG.getFoodRepair(feedItem.getType());
        if (repair == null) {
            SummonMounts.LOGGER.info("Repair is null");
            return ActionResult.PASS;
        }

        if (mountOptional.get().repairItem(repair)) {
            stack.decrement(1);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }
}
