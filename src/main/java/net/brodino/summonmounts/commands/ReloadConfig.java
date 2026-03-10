package net.brodino.summonmounts.commands;

import com.mojang.brigadier.context.CommandContext;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadConfig {
    public static int execute(CommandContext<ServerCommandSource> context) {
        SummonMounts.CONFIG.reload();
        context.getSource().sendMessage(Text.literal(SummonMounts.MOD_ID + " config reloaded"));
        return 1;
    }
}
