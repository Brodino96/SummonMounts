package dev.brodino.summonmounts;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandHandler {

    public static void initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess _ra, CommandManager.RegistrationEnvironment _re) {
        dispatcher.register(CommandManager.literal("mount")
                .requires(src -> src.hasPermissionLevel(2))
                .then(getReloadCommand())
        );

        MountCommand.register(dispatcher);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> getReloadCommand() {
        return CommandManager.literal("reload")
                .executes(context -> {
                    if (SummonMounts.CONFIG.reload()) {
                        context.getSource().sendFeedback(Text.literal("Config reloaded"), true);
                        return 1;
                    } else {
                        context.getSource().sendFeedback(Text.literal("Failed to reload config").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
                        return 0;
                    }
                });
    }
}
