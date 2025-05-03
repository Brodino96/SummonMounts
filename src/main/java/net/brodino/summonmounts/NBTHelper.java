package net.brodino.summonmounts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class NBTHelper {
    /**
     * Saves mount data to an item's NBT
     * @param player The player who owns the mount
     * @param mount The mount entity
     * @param stack The item stack to save the data to
     * @return The updated ItemStack with mount data
     */
    public static ItemStack saveMountData(PlayerEntity player, Entity mount, ItemStack stack) {
        if (!(mount instanceof AbstractHorseEntity)) {
            return stack;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound mountData = new NbtCompound();
        mount.writeNbt(mountData);
        
        // Store basic mount data
        String entityId = Registry.ENTITY_TYPE.getId(mount.getType()).toString();
        UUID mountItemId = UUID.randomUUID();
        nbt.putUuid("mount.id", mountItemId);
        nbt.put("mount.data", mountData);
        Summonmounts.LOGGER.info(mountData.toString());
        nbt.putString("mount.type", entityId);
        nbt.putUuid("mount.owner", player.getUuid());

        String name = mount.getName().getString();
        if (mount.hasCustomName()) {
            name = mount.getCustomName().getString();
        }
        nbt.putString("mount.name", name);

        // Save equipment data
        saveEquipmentData((AbstractHorseEntity) mount, nbt);

        return stack;
    }

    /**
     * Saves equipment data to the NBT compound
     * @param horse The horse entity
     * @param nbt The NBT compound to save to
     */
    private static void saveEquipmentData(AbstractHorseEntity horse, NbtCompound nbt) {
        NbtCompound equipmentData = new NbtCompound();
        
        // Save saddle state
        equipmentData.putBoolean("equipment.saddle", horse.isSaddled());
        
        // Save armor for regular horses
        if (horse instanceof HorseEntity) {
            HorseEntity regularHorse = (HorseEntity) horse;
            ItemStack armorStack = regularHorse.getEquippedStack(EquipmentSlot.CHEST);
            
            if (!armorStack.isEmpty()) {
                NbtCompound armorNbt = new NbtCompound();
                armorStack.writeNbt(armorNbt);
                equipmentData.put("equipment.armor", armorNbt);
            }
        }
        
        nbt.put("mount.equipment", equipmentData);
    }

    /**
     * Loads mount data from NBT to an entity
     * @param mount The entity to load data into
     * @param nbt The NBT compound containing the mount data
     * @return The updated entity with loaded data
     */
    public static Entity loadMountData(Entity mount, NbtCompound nbt) {
        if (!(mount instanceof AbstractHorseEntity) || !nbt.contains("mount.data")) {
            return mount;
        }

        // Load basic mount data
        NbtCompound mountData = nbt.getCompound("mount.data");
        Summonmounts.LOGGER.info(mountData.toString());
        mount.readNbt(mountData);

        // Set custom name if present
        if (nbt.contains("mount.name")) {
            mount.setCustomName(Text.literal(nbt.getString("mount.name")));
        }

        // Load equipment data
        if (nbt.contains("mount.equipment")) {
            loadEquipmentData((AbstractHorseEntity) mount, nbt);
        }

        return mount;
    }

    /**
     * Loads equipment data from NBT to a horse entity
     * @param horse The horse entity to load equipment into
     * @param nbt The NBT compound containing the equipment data
     */
    private static void loadEquipmentData(AbstractHorseEntity horse, NbtCompound nbt) {
        NbtCompound equipmentData = nbt.getCompound("mount.equipment");
        
        // Load saddle state
        if (equipmentData.contains("equipment.saddle")) {
            horse.saddle(null);
        }
        
        // Load armor for regular horses
        if (horse instanceof HorseEntity && equipmentData.contains("equipment.armor")) {
            HorseEntity regularHorse = (HorseEntity) horse;
            NbtCompound armorNbt = equipmentData.getCompound("equipment.armor");
            ItemStack armorStack = ItemStack.fromNbt(armorNbt);
            regularHorse.equipStack(EquipmentSlot.CHEST, armorStack);
        }
    }
}