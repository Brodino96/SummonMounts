package dev.brodino.summonmounts.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType FAILED_UUID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.invalidPosition"));

    public static LiteralArgumentBuilder<ServerCommandSource> getCommand() {
        return CommandManager.literal("summon")
            .then(CommandManager.argument("entity", EntitySummonArgumentType.entitySummon())
                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                .executes(context -> execute(context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), context.getSource().getPosition(), new NbtCompound(), true))
                .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                    .executes(context -> execute(context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), Vec3ArgumentType.getVec3(context, "pos"), new NbtCompound(), true))
                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                        .executes(context -> execute(context.getSource(), EntitySummonArgumentType.getEntitySummon(context, "entity"), Vec3ArgumentType.getVec3(context, "pos"), NbtCompoundArgumentType.getNbtCompound(context, "nbt"), false)
                        )
                    )
                )
            );
    }

    private static int execute(ServerCommandSource source, Identifier entity, Vec3d pos, NbtCompound nbt, boolean initialize) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(pos);
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }

        NbtCompound nbtCompound = nbt.copy();
        nbtCompound.putString("id", entity.toString());
        ServerWorld serverWorld = source.getWorld();
        Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, (entityx) -> {
            entityx.refreshPositionAndAngles(pos.x, pos.y, pos.z, entityx.getYaw(), entityx.getPitch());
            return entityx;
        });

        if (entity2 instanceof AbstractHorseEntity h) {
            h.setTemper(100);
            h.saddle(null);
        }

        if (entity2 == null) {
            throw FAILED_EXCEPTION.create();
        }

        if (initialize && entity2 instanceof MobEntity) {
            ((MobEntity)entity2).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(entity2.getBlockPos()), SpawnReason.COMMAND, (EntityData)null, (NbtCompound)null);
        }

        if (!serverWorld.spawnNewEntityAndPassengers(entity2)) {
            throw FAILED_UUID_EXCEPTION.create();
        }

        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            player.startRiding(entity2, true);
        }
        source.sendFeedback(Text.translatable("commands.summon.success", new Object[]{entity2.getDisplayName()}), true);
        return 1;
    }
}
