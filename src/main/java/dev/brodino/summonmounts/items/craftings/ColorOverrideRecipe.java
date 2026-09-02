package dev.brodino.summonmounts.items.craftings;

import com.google.gson.JsonObject;
import dev.brodino.summonmounts.items.ItemManager;
import dev.brodino.summonmounts.items.OcarinaItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ColorOverrideRecipe extends ShapelessRecipe {

    public ColorOverrideRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
        super(id, group, output, input);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack customItem = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        int extra = 0;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof OcarinaItem) {
                customItem = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                if (!dye.isEmpty()) return false;
                dye = stack;
            } else {
                extra++;
            }
        }

        return !customItem.isEmpty() && !dye.isEmpty() && extra == 0;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack customItem = ItemStack.EMPTY;
        DyeItem dyeItem = null;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof OcarinaItem) {
                customItem = stack;
            } else if (stack.getItem() instanceof DyeItem dye) {
                dyeItem = dye;
            }
        }

        if (customItem.isEmpty() || dyeItem == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = customItem.copy();
        result.setCount(1);

        NbtCompound nbt = result.getOrCreateNbt();
        nbt.putInt("Color", dyeItem.getColor().getFireworkColor());

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ItemManager.COLOR_OVERRIDE;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    public static class Serializer implements RecipeSerializer<ColorOverrideRecipe> {

        @Override
        public ColorOverrideRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            DefaultedList<Ingredient> ingredients = DefaultedList.of();

            var array = JsonHelper.getArray(json, "ingredients");
            for (int i = 0; i < array.size(); i++) {
                ingredients.add(Ingredient.fromJson(array.get(i)));
            }

            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            return new ColorOverrideRecipe(id, group, output, ingredients);
        }

        @Override
        public ColorOverrideRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString();
            int size = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            return new ColorOverrideRecipe(id, group, output, ingredients);
        }

        @Override
        public void write(PacketByteBuf buf, ColorOverrideRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput());
        }
    }
}
