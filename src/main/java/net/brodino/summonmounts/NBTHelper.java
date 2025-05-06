package net.brodino.summonmounts;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.registry.Registry;

public class NBTHelper {

    public static ItemStack saveMountData(Entity entity, ItemStack stack) {
        if (!(entity instanceof AbstractHorseEntity mount)) {
            SummonMounts.LOGGER.info("Entity is not a Horse derivative");
            return stack;
        }

        SummonMounts.LOGGER.info("Item nbt's before doing anything: {}", stack.getNbt().toString());

        NbtCompound stackNbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        mount.writeNbt(mountNbt);
        SummonMounts.LOGGER.info("Created nbts");

        if (mountNbt.contains("ArmorItems")) {
            SummonMounts.LOGGER.info("Mount armor is: {}", mountNbt.get("ArmorItems").toString());
            stackNbt.put("mount.armor", mountNbt.get("ArmorItems"));
            mountNbt.remove("ArmorItems");
        }

        if (mountNbt.contains("SaddleItem")) {
            SummonMounts.LOGGER.info("Mount saddle is: {}", mountNbt.get("SaddleItem").toString());
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

        SummonMounts.LOGGER.info("Item nbt's after doing everything: {}", stack.getNbt().toString());

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