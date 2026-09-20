package dev.brodino.summonmounts.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.ItemManager;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ConvertCommand {
    public static final String CONVERTED_TAG = SummonMounts.MOD_ID + ":legacy_converted";

    private static final String LEGACY_TYPE = "mount.type";
    private static final String LEGACY_GENERIC_DATA = "mount.genericData";
    private static final String LEGACY_ARMOR = "mount.armor";
    private static final String LEGACY_SADDLE = "mount.saddle";
    private static final String LEGACY_DECOR = "mount.decor";
    private static final String LEGACY_OWNER = "mount.owner";
    private static final String LEGACY_NAME = "mount.name";
    private static final String LEGACY_VARIANT = "mount.variant";

    public static LiteralArgumentBuilder<ServerCommandSource> getCommand() {
        return CommandManager.literal("convert")
                .executes(context -> execute(context.getSource()));
    }

    private static int execute(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.translatable("commands.summonmounts.convert.player_only"));
            return 0;
        }

        ItemStack legacyStack = player.getMainHandStack();
        NbtCompound legacyNbt = legacyStack.getNbt();
        if (legacyStack.isEmpty() || legacyNbt == null) {
            source.sendError(Text.translatable("commands.summonmounts.convert.no_item"));
            return 0;
        }

        if (legacyNbt.contains(CONVERTED_TAG)) {
            source.sendError(Text.translatable("commands.summonmounts.convert.already_converted"));
            return 0;
        }

        Optional<ConvertedOcarina> conversion = convert(legacyNbt);
        if (conversion.isEmpty()) {
            source.sendError(Text.translatable("commands.summonmounts.convert.invalid_item"));
            return 0;
        }

        ConvertedOcarina converted = conversion.get();
        String itemName = converted.stack().getName().getString();
        player.giveItemStack(converted.stack());
        legacyNbt.putBoolean(CONVERTED_TAG, true);

        source.sendFeedback(Text.translatable(
                "commands.summonmounts.convert.success",
                itemName), true);
        return 1;
    }

    private static Optional<ConvertedOcarina> convert(NbtCompound legacyNbt) {
        if (!legacyNbt.contains(LEGACY_TYPE, NbtElement.STRING_TYPE)) {
            return Optional.empty();
        }

        String mountType = legacyNbt.getString(LEGACY_TYPE);
        if (Identifier.tryParse(mountType) == null) {
            return Optional.empty();
        }

        Optional<OcarinaTypes> ocarinaType = SummonMounts.CONFIG.getOcarinaType(mountType);
        if (ocarinaType.isEmpty()) {
            return Optional.empty();
        }

        NbtCompound mountData = new NbtCompound();
        mountData.putString("type", mountType);

        NbtCompound genericData = legacyNbt.contains(LEGACY_GENERIC_DATA, NbtElement.COMPOUND_TYPE)
                ? legacyNbt.getCompound(LEGACY_GENERIC_DATA)
                : new NbtCompound();

        copyIfPresent(genericData, mountData, "CustomName", NbtElement.STRING_TYPE);
        copyIfPresent(genericData, mountData, "Owner", NbtElement.INT_ARRAY_TYPE);
        copyIfPresent(genericData, mountData, "Variant");
        copyIfPresent(genericData, mountData, "Tame", NbtElement.BYTE_TYPE);
        copyIfPresent(genericData, mountData, "ArmorItem", NbtElement.COMPOUND_TYPE);
        copyIfPresent(genericData, mountData, "SaddleItem", NbtElement.COMPOUND_TYPE);
        copyIfPresent(genericData, mountData, "DecorItem", NbtElement.COMPOUND_TYPE);

        copyIfAbsent(legacyNbt, mountData, LEGACY_OWNER, "Owner", NbtElement.INT_ARRAY_TYPE);
        copyIfAbsent(legacyNbt, mountData, LEGACY_VARIANT, "Variant");
        copyIfAbsent(legacyNbt, mountData, LEGACY_NAME, "CustomName", NbtElement.STRING_TYPE);
        copyIfAbsent(legacyNbt, mountData, LEGACY_ARMOR, "ArmorItem", NbtElement.COMPOUND_TYPE);
        copyIfAbsent(legacyNbt, mountData, LEGACY_SADDLE, "SaddleItem", NbtElement.COMPOUND_TYPE);
        copyIfAbsent(legacyNbt, mountData, LEGACY_DECOR, "DecorItem", NbtElement.COMPOUND_TYPE);
        if (mountData.contains("Owner") && !mountData.contains("Tame")) {
            mountData.putBoolean("Tame", true);
        }

        ItemStack ocarina = new ItemStack(ItemManager.getOcarinaFromEnum(ocarinaType.get()));
        ocarina.getOrCreateNbt().put(SummonMounts.MOD_ID, mountData);
        return Optional.of(new ConvertedOcarina(ocarina));
    }

    private static void copyIfPresent(NbtCompound source, NbtCompound target, String key) {
        NbtElement value = source.get(key);
        if (value != null) {
            target.put(key, value.copy());
        }
    }

    private static void copyIfPresent(NbtCompound source, NbtCompound target, String key, int type) {
        if (source.contains(key, type)) {
            copyIfPresent(source, target, key);
        }
    }

    private static void copyIfAbsent(NbtCompound source, NbtCompound target, String sourceKey, String targetKey, int type) {
        if (target.contains(targetKey)) {
            return;
        }

        if (source.contains(sourceKey, type)) {
            NbtElement value = source.get(sourceKey);
            if (value != null) {
                target.put(targetKey, value.copy());
            }
        }
    }

    private static void copyIfAbsent(NbtCompound source, NbtCompound target, String sourceKey, String targetKey) {
        if (!target.contains(targetKey)) {
            copyIfPresent(source, target, sourceKey);
        }
    }

    private record ConvertedOcarina(ItemStack stack) {}
}
