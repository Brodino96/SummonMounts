package net.brodino.summonmounts.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class SpawnRandomHorseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spawnhorse")
            .requires(source -> source.hasPermissionLevel(2)) // Requires permission level 2 (like game operators)
            .executes(SpawnRandomHorseCommand::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendError(Text.literal("This command must be executed by a player"));
            return 0;
        }

        // Create a new horse
        HorseEntity horse = new HorseEntity(net.minecraft.entity.EntityType.HORSE, player.getWorld());

        // Set random attributes
        Random random = player.getWorld().getRandom();
        
        // Speed between 0.1125 and 0.3375 (vanilla horse speeds)
        double speed = 0.1125 + (random.nextDouble() * 0.225);
        horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

        // Jump strength between 0.4 and 1.0 (vanilla horse jump strengths)
        double jumpStrength = 0.4 + (random.nextDouble() * 0.6);
        horse.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(jumpStrength);

        // Health between 15 and 30 (vanilla horse health)
        double health = 15.0 + (random.nextDouble() * 15.0);
        horse.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(health);
        horse.setHealth((float)health);

        // Set position to player's location
        horse.setPosition(player.getX(), player.getY(), player.getZ());

        // Make the horse tamed and set the player as owner
        horse.setTame(true);
        horse.setOwnerUuid(player.getUuid());

        // Add saddle
        horse.saddle(null);

        // Spawn the horse in the world
        player.getWorld().spawnEntity(horse);

        // Send success message
        //source.sendFeedback(() -> Text.literal("Spawned a random tamed horse!"), true);

        return 1;
    }
}