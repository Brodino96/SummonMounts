package net.brodino.summonmounts;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AntiAbuse {

    public static void initialize() {
        UseItemCallback.EVENT.register(AntiAbuse::onItemUse);
        AttackEntityCallback.EVENT.register(AntiAbuse::onEntityAttack);
    }

    private static ActionResult onEntityAttack(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (canHeDoThat(player, stack)) {
            return ActionResult.PASS;
        }
        notifyPlayer(player);
        return ActionResult.FAIL;
    }

    private static TypedActionResult<ItemStack> onItemUse(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (canHeDoThat(player, stack)) {
            return TypedActionResult.pass(stack);
        }
        notifyPlayer(player);
        return TypedActionResult.fail(stack);
    }


    public static boolean canHeDoThat(PlayerEntity player, ItemStack stack) {
        if (!player.hasVehicle()) {
            return true;
        }

        Entity vehicle = player.getVehicle();

        if (vehicle == null) {
            return true;
        }

        String itemId = Registry.ITEM.getId(stack.getItem()).toString();
        String mountId = Registry.ENTITY_TYPE.getId(vehicle.getType()).toString();

        for (String allowedType : SummonMounts.CONFIG.allowedSummons()) {
            if (allowedType.equals(mountId) && SummonMounts.CONFIG.blockedItems().contains(itemId)) {
                return false;
            }
        }

        return true;
    }

    public static void notifyPlayer(PlayerEntity player) {
        player.sendMessage(Text.literal(SummonMounts.CONFIG.locales().itemUse.whileRiding), true);
    }
}
