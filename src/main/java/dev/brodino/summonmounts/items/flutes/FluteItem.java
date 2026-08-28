package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class FluteItem extends Item {

    public static final Settings BASE_SETTINGS = new Settings()
            .group(ItemGroup.TOOLS)
            .fireproof()
            .maxCount(1);

    public FluteItem(Settings settings) {
        super(settings);
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
