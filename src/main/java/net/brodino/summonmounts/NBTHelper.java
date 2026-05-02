package net.brodino.summonmounts;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class NBTHelper {

    public static void setCustomLore(ItemStack stack, String description) {

        if (stack == null || description == null) {
            return;
        }

        NbtList loreList = new NbtList();

        loreList.add(NbtString.of(Text.Serializer.toJson(Text.of(description))));

        stack.getOrCreateSubNbt("display").put("Lore", loreList);
    }

    /**
     * Saves the mount data inside the ItemStack.
     * Only saves essential fields: health, saddle, armor, name, owner, uuid, type, and variant.
     * @param entity The mount
     * @param stack The ItemStack where to save data
     * @param dead Whether the mount is dead (if true, health/saddle/armor are not saved)
     * @return The ItemStack
     */
    public static ItemStack saveMountData(Entity entity, ItemStack stack, boolean dead) {

        if (!(entity instanceof AbstractHorseEntity mount)) {
            return stack;
        }

        if (stack == null) {
            return null;
        }

        NbtCompound stackNbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        mount.writeNbt(mountNbt);

        // Save health (unless dead)
        if (!dead) {
            stackNbt.putFloat("mount.health", mount.getHealth());
        }

        // Save armor (unless dead)
        // Horse armor is stored in "ArmorItem" (singular), general armor in "ArmorItems" (plural)
        if (!dead) {
            if (mountNbt.contains("ArmorItem")) {
                NbtElement armorData = mountNbt.get("ArmorItem");
                if (armorData != null) {
                    stackNbt.put("mount.armor", armorData);
                }
            } else if (mountNbt.contains("ArmorItems")) {
                NbtElement armorData = mountNbt.get("ArmorItems");
                if (armorData != null) {
                    stackNbt.put("mount.armorItems", armorData);
                }
            }
        }

        // Save saddle (unless dead)
        if (!dead && mountNbt.contains("SaddleItem")) {
            NbtElement saddleData = mountNbt.get("SaddleItem");
            if (saddleData != null) {
                stackNbt.put("mount.saddle", saddleData);
            }
        }

        // Save custom name
        if (mount.getCustomName() != null) {
            String customName = mount.getCustomName().toString();
            stackNbt.putString("mount.name", customName);
        }

        // Save owner UUID (if present)
        if (mount.getOwnerUuid() != null) {
            stackNbt.putUuid("mount.owner", mount.getOwnerUuid());
        }

        // Save mount UUID
        stackNbt.putUuid("mount.uuid", mount.getUuid());

        // Save entity type
        stackNbt.putString("mount.type", Registry.ENTITY_TYPE.getId(mount.getType()).toString());

        // Save variant (for visual appearance - horses, llamas, etc.)
        if (mountNbt.contains("Variant")) {
            stackNbt.putInt("mount.variant", mountNbt.getInt("Variant"));
        }

        // Save llama decor item if present
        if (mountNbt.contains("DecorItem")) {
            stackNbt.put("mount.decor", mountNbt.get("DecorItem"));
        }

        stack.setNbt(stackNbt);
        stack.addEnchantment(Enchantments.LOYALTY, 1);

        return stack;

    }

    /**
     * Loading the mount data from the item NBTs.
     * Supports both old format (with mount.genericData) and new format.
     * Old items are migrated by converting to new format immediately.
     * @param mount The mount
     * @param nbt Data (the item's NBT - will be modified for migration)
     * @return The mount
     */
    public static Entity loadMountData(AbstractHorseEntity mount, NbtCompound nbt) {

        // Handle old format (with mount.genericData) - migration path
        if (nbt.contains("mount.genericData")) {
            NbtCompound mountNbt = nbt.getCompound("mount.genericData");

            // Re-inject armor and saddle into the generic data for readNbt
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

            // === MIGRATION: Convert old format to new format immediately ===
            // Remove old genericData
            nbt.remove("mount.genericData");
            nbt.remove("mount.armor");
            nbt.remove("mount.saddle");

            // Save health
            nbt.putFloat("mount.health", mount.getHealth());

            // Save variant if present in the old data
            if (mountNbt.contains("Variant")) {
                nbt.putInt("mount.variant", mountNbt.getInt("Variant"));
            }

            // Save horse armor (ArmorItem - singular)
            if (mountNbt.contains("ArmorItem")) {
                nbt.put("mount.armor", mountNbt.get("ArmorItem"));
            }

            // Save general armor (ArmorItems - plural)
            if (mountNbt.contains("ArmorItems")) {
                nbt.put("mount.armorItems", mountNbt.get("ArmorItems"));
            }

            // Save saddle
            if (mountNbt.contains("SaddleItem")) {
                nbt.put("mount.saddle", mountNbt.get("SaddleItem"));
            }

            // Save llama decor
            if (mountNbt.contains("DecorItem")) {
                nbt.put("mount.decor", mountNbt.get("DecorItem"));
            }

            return mount;
        }

        // Handle new format (without mount.genericData)
        if (!nbt.contains("mount.uuid")) {
            return mount;
        }

        // Set UUID
        mount.setUuid(nbt.getUuid("mount.uuid"));

        // Set owner UUID
        if (nbt.contains("mount.owner")) {
            mount.setOwnerUuid(nbt.getUuid("mount.owner"));
            mount.setTame(true);
        }

        // Set health
        if (nbt.contains("mount.health")) {
            mount.setHealth(nbt.getFloat("mount.health"));
        }

        // Set variant (horse color/markings, llama color, etc.)
        if (nbt.contains("mount.variant")) {
            NbtCompound variantNbt = new NbtCompound();
            mount.writeNbt(variantNbt);
            variantNbt.putInt("Variant", nbt.getInt("mount.variant"));
            mount.readNbt(variantNbt);
        }

        // Apply horse armor (ArmorItem - singular)
        if (nbt.contains("mount.armor")) {
            NbtCompound armorNbt = new NbtCompound();
            mount.writeNbt(armorNbt);
            armorNbt.put("ArmorItem", nbt.get("mount.armor"));
            mount.readNbt(armorNbt);
        }

        // Apply general armor items (ArmorItems - plural, for other mobs)
        if (nbt.contains("mount.armorItems")) {
            NbtCompound armorNbt = new NbtCompound();
            mount.writeNbt(armorNbt);
            armorNbt.put("ArmorItems", nbt.get("mount.armorItems"));
            mount.readNbt(armorNbt);
        }

        // Apply saddle
        if (nbt.contains("mount.saddle")) {
            NbtCompound saddleNbt = new NbtCompound();
            mount.writeNbt(saddleNbt);
            saddleNbt.put("SaddleItem", nbt.get("mount.saddle"));
            mount.readNbt(saddleNbt);
        }

        // Apply llama decor
        if (nbt.contains("mount.decor")) {
            NbtCompound decorNbt = new NbtCompound();
            mount.writeNbt(decorNbt);
            decorNbt.put("DecorItem", nbt.get("mount.decor"));
            mount.readNbt(decorNbt);
        }

        return mount;
    }

    /**
     * Re-applies the saved variant (color/markings) to a horse after it has been spawned.
     * Called after world.spawnEntity() to ensure the variant is not overwritten by spawn logic.
     */
    public static void applyVariant(AbstractHorseEntity mount, NbtCompound nbt) {
        if (nbt == null || !nbt.contains("mount.variant")) {
            return;
        }
        NbtCompound variantNbt = new NbtCompound();
        mount.writeNbt(variantNbt);
        variantNbt.putInt("Variant", nbt.getInt("mount.variant"));
        mount.readNbt(variantNbt);
    }
}