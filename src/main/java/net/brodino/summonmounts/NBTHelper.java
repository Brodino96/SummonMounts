package net.brodino.summonmounts;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;

public class NBTHelper {

    public static ItemStack saveMountData(Entity entity, ItemStack stack) {
        if (!(entity instanceof AbstractHorseEntity mount)) {
            return stack;
        }

        NbtCompound stackNbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        mount.writeNbt(mountNbt);

        if (mountNbt.contains("ArmorItems")) {
            stackNbt.put("mount.armor", mountNbt.get("ArmorItems"));
            mountNbt.remove("ArmorItems");
        }

        if (mountNbt.contains("SaddleItem")) {
            stackNbt.put("mount.saddle", mountNbt.getCompound("SaddleItem"));
            mountNbt.remove("SaddleItem");
        }

        if (mount.getCustomName() != null) {
            stackNbt.putString("mount.name", mount.getCustomName().toString());
        }

        stackNbt.putUuid("mount.owner", ((AbstractHorseEntity) mount).getOwnerUuid());

        stackNbt.putString("mount.type", Registry.ENTITY_TYPE.getId(mount.getType()).toString());
        stackNbt.put("mount.genericData", mountNbt);

        stack.setNbt(stackNbt);
        stack.addEnchantment(Enchantments.LOYALTY, 1);

        return stack;

    }

    public static Entity loadMountData(AbstractHorseEntity mount, NbtCompound nbt) {

        if (!nbt.contains("mount.genericData")) {
            return mount;
        }

        NbtCompound mountNbt = nbt.getCompound("mount.genericData");

        if (nbt.contains("mount.armor")) {
            mountNbt.put("ArmorItems", nbt.getCompound("mount.armor"));
        }

        if (nbt.contains("mount.saddle")) {
            mountNbt.put("SaddleItem", nbt.getCompound("mount.saddle"));
        }

        mount.readNbt(mountNbt);
        return mount;
    }
}