package net.brodino.summonmounts;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class NBTHelper {

    /**
     * Saves the mount data inside the ItemStack
     * @param entity The mount
     * @param stack The ItemStack where to save data
     * @return The ItemStack
     */
    public static ItemStack saveMountData(Entity entity, ItemStack stack) {
        if (!(entity instanceof AbstractHorseEntity mount)) {
            SummonMounts.LOGGER.warn("Entity is not a Horse derivative");
            return stack;
        }

        NbtCompound stackNbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        mount.writeNbt(mountNbt);
        SummonMounts.LOGGER.info("NBTs generated");

        if (mountNbt.contains("ArmorItems")) {
            NbtElement armorData = mountNbt.get("ArmorItems");
            SummonMounts.LOGGER.info("Mount armor is: {}", armorData.toString());
            stackNbt.put("mount.armor", armorData);
            mountNbt.remove("ArmorItems");
        }

        if (mountNbt.contains("SaddleItem")) {
            NbtElement saddleData = mountNbt.get("SaddleItem");
            SummonMounts.LOGGER.info("Mount saddle is: {}", saddleData.toString());
            stackNbt.put("mount.saddle", saddleData);
            mountNbt.remove("SaddleItem");
        }

        if (mount.getCustomName() != null) {
            String customName = mount.getCustomName().toString();
            SummonMounts.LOGGER.info(customName);
            stackNbt.putString("mount.name", customName);
        }

        stackNbt.putUuid("mount.owner", ((AbstractHorseEntity) mount).getOwnerUuid());

        stackNbt.putString("mount.type", Registry.ENTITY_TYPE.getId(mount.getType()).toString());
        stackNbt.put("mount.genericData", mountNbt);

        stack.setNbt(stackNbt);
        stack.addEnchantment(Enchantments.LOYALTY, 1);

        return stack;

    }

    /**
     * Loading the mount data from the item NBTs
     * @param mount The mount
     * @param nbt Data
     * @return The mount
     */
    public static Entity loadMountData(AbstractHorseEntity mount, NbtCompound nbt) {

        SummonMounts.LOGGER.info("Starting to load mount data");

        if (!nbt.contains("mount.genericData")) {
            SummonMounts.LOGGER.warn("Item didn't have the correct data");
            return mount;
        }

        NbtCompound mountNbt = nbt.getCompound("mount.genericData");

        if (nbt.contains("mount.armor")) {
            NbtElement armorData = nbt.get("mount.armor");
            SummonMounts.LOGGER.info("Item has this armor saved: {}", armorData.toString());
            mountNbt.put("ArmorItems", armorData);
        }

        if (nbt.contains("mount.saddle")) {
            NbtElement saddleData = nbt.get("mount.saddle");
            SummonMounts.LOGGER.info("Item has this saddle saved: {}", saddleData.toString());
            mountNbt.put("SaddleItem", saddleData);
        }

        SummonMounts.LOGGER.info("Loading the data inside the mount");
        mount.readNbt(mountNbt);
        return mount;
    }
}