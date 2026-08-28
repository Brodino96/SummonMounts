package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public class FluteItem extends Item {

    public static final Settings BASE_SETTINGS = new Settings()
            .group(ItemGroup.TOOLS)
            .fireproof()
            .maxCount(1);

    public FluteItem(Settings settings) { super(settings); }

    @Environment(EnvType.SERVER)
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!containsMount(stack)) {
            return TypedActionResult.fail(stack);
        }

        // Combat log check

        if (!SummonMounts.CONFIG.getAllowedDimensions().contains(world.getRegistryKey().getValue().toString())) {
            return TypedActionResult.fail(stack);
        }

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        // if active mount and linked to this, recall
        // if active mount and not linked to this, fail
        // if no active mount and not in cooldown, summon

        player.getItemCooldownManager().set(this, SummonMounts.CONFIG.getFluteCooldown() * 20);
        // play sound
        Optional<Mount> mount = Mount.fromStack(player, stack);
        if (mount.isEmpty()) {
            return TypedActionResult.fail(stack);
        }
        mount.get().summon();
        return TypedActionResult.success(stack);
    }

    private TypedActionResult<ItemStack> summon(ItemStack stack) {
        return TypedActionResult.success(stack);
    }

    private TypedActionResult<ItemStack> recall(ItemStack stack) {
        return TypedActionResult.success(stack);
    }

    // Nbt stuff

    /**
     * Checks if the {@link NbtCompound} is linked to a {@link Mount}
     * @param nbt The {@link NbtCompound} to check
     * @return true if it finds data linked to a {@link Mount}
     */
    public static boolean containsMount(NbtCompound nbt) {
        return nbt != null && nbt.contains(SummonMounts.MOD_ID);
    }

    public static boolean containsMount(ItemStack stack) {
        return containsMount(stack.getNbt());
    }

    public static NbtCompound getMountData(ItemStack stack) {
        final NbtCompound nbt = stack.getNbt();
        return !containsMount(nbt) ? null : nbt.getCompound(SummonMounts.MOD_ID);
    }

    /**
     * Saves the {@link Mount} inside the {@link FluteItem}
     * @param stack {@link ItemStack} of the {@link FluteItem}
     * @param mount The {@link Mount}
     */
    public static void saveMount(ItemStack stack, Mount mount) {
        NbtCompound mountData = mount.getSavableNbt();
        stack.getOrCreateNbt().put(SummonMounts.MOD_ID, mountData);
        stack.addEnchantment(Enchantments.LOYALTY, 1);
        // ItemLore
    }


}
