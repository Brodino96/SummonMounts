package net.brodino.summonmounts;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.registry.Registry;

public class NBTHelper {

    /**
     * Saves the mount data inside the ItemStack
     * @param entity The mount
     * @param stack The ItemStack where to save data
     * @return The ItemStack
     */
    public static ItemStack saveMountData(Entity entity, ItemStack stack, boolean dead) {

        if (!(entity instanceof AbstractHorseEntity mount)) {
            return stack;
        }

        NbtCompound stackNbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        mount.writeNbt(mountNbt);

        if (dead) {
            mountNbt.remove("Health");

            mountNbt.remove("SaddleItem");
            mountNbt.remove("ArmorItems");
            mountNbt.remove("ArmorItem");
        }

        if (mountNbt.contains("ArmorItems")) {
            NbtElement armorData = mountNbt.get("ArmorItems");
            if (armorData != null) {
                stackNbt.put("mount.armor", armorData);
                mountNbt.remove("ArmorItems");
            }
        }

        if (mountNbt.contains("SaddleItem")) {
            NbtElement saddleData = mountNbt.get("SaddleItem");
            if (saddleData != null) {
                stackNbt.put("mount.saddle", saddleData);
                mountNbt.remove("SaddleItem");
            }
        }

        if (mount.getCustomName() != null) {
            String customName = mount.getCustomName().toString();
            stackNbt.putString("mount.name", customName);
        }

        stackNbt.putUuid("mount.owner", mount.getOwnerUuid());
        stackNbt.putUuid("mount.uuid", mount.getUuid());

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

        if (!nbt.contains("mount.genericData")) {
            return mount;
        }

        NbtCompound mountNbt = nbt.getCompound("mount.genericData");

        if (nbt.contains("mount.armor")) {
            NbtElement armorData = nbt.get("mount.armor");
            mountNbt.put("ArmorItems", armorData);
        }

        if (nbt.contains("mount.saddle")) {
            NbtElement saddleData = nbt.get("mount.saddle");
            mountNbt.put("SaddleItem", saddleData);
        }

        mount.setUuid(nbt.getUuid("mount.uuid"));

        mount.readNbt(mountNbt);
        return mount;
    }
}