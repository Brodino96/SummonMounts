package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.Utils;
import dev.brodino.summonmounts.mount.Mount;
import dev.brodino.summonmounts.mount.RecallReason;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public class OcarinaItem extends SummonMountsItem {

    public static final Settings BASE_SETTINGS = new Settings()
            .group(ItemGroup.TOOLS)
            .fireproof()
            .maxCount(1)
            .maxDamage(32);

    public OcarinaItem(Settings settings, OcarinaTypes type) { super(settings, type); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient()) {
            return TypedActionResult.pass(stack);
        }

        if (!containsMount(stack)) {
            return TypedActionResult.fail(stack);
        }

        if (!SummonMounts.CONFIG.getAllowedDimensions().contains(world.getRegistryKey().getValue().toString())) {
            return TypedActionResult.fail(stack);
        }

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        Mount mount = MountManager.getActiveMount(player);
        return mount == null
                ? this.summon(player, stack)
                : this.recall(player, stack, mount);
    }

    private TypedActionResult<ItemStack> summon(PlayerEntity player, ItemStack stack) {
        if (Utils.combatLogCheck(player)) {
            return TypedActionResult.fail(stack);
        }

        Optional<Mount> mount = Mount.fromStack(player, stack);
        if (mount.isEmpty()) {
            return TypedActionResult.fail(stack);
        }

        if (MountManager.summon(player, mount.get())) {
            if (this.getDurability(stack) > 1) {
                final int newDamage = stack.getDamage() + 1;
                Criteria.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) player, stack, newDamage);
                stack.setDamage(newDamage);
            }
        }

        this.setCooldown(player);
        return TypedActionResult.success(stack);
    }

    private int getDurability(ItemStack stack) { return stack.getMaxDamage() - stack.getDamage(); }

    private TypedActionResult<ItemStack> recall(PlayerEntity player, ItemStack stack, Mount mount) {
        if (!mount.getStack().equals(stack)) {
            return TypedActionResult.fail(stack);
        }
        MountManager.recall(player, RecallReason.MANUAL);
        return TypedActionResult.success(stack);
    }

    private void setCooldown(PlayerEntity player) { player.getItemCooldownManager().set(this, SummonMounts.CONFIG.getOcarinaCooldown() * 20); }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        final PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.FAIL;
        if (MountManager.hasActiveMount(player)) {
            return ActionResult.FAIL;
        }
        return super.useOnBlock(context);
    }

    // Nbt stuff

    public static boolean containsMount(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains(SummonMounts.MOD_ID);
    }


    public static void saveMount(ItemStack stack, Mount mount) {
        NbtCompound mountData = mount.getSavableNbt();
        stack.getOrCreateNbt().put(SummonMounts.MOD_ID, mountData);
        // ItemLore
    }

}
