package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

    @Override
    @Environment(EnvType.SERVER)
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

        Mount mount = MountManager.getActiveMount(player);
        return mount == null
                ? this.summon(player, stack)
                : this.recall(player, stack, mount);
    }

    private TypedActionResult<ItemStack> summon(PlayerEntity player, ItemStack stack) {
        Optional<Mount> mount = Mount.fromStack(player, stack);
        if (mount.isEmpty()) {
            return TypedActionResult.fail(stack);
        }

        MountManager.summon(player, mount.get());
        // play sound

        this.setCooldown(player);
        return TypedActionResult.success(stack);

    }

    private TypedActionResult<ItemStack> recall(PlayerEntity player, ItemStack stack, Mount mount) {
        if (!mount.getStack().equals(stack)) {
            return TypedActionResult.fail(stack);
        }
        MountManager.recall(player);
        return TypedActionResult.success(stack);
    }

    private void setCooldown(PlayerEntity player) { player.getItemCooldownManager().set(this, SummonMounts.CONFIG.getFluteCooldown() * 20); }

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
        // ItemLore
    }

}
