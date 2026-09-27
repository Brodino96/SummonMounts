package dev.brodino.summonmounts.ledger;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class LedgerManager {

	public static void initialize(MinecraftServer server) {
		if (!SummonMounts.LEDGER_PRESENT) return;
		LedgerHook.initialize();
	}

	public static void logSummon(Mount mount) {
		if (!SummonMounts.LEDGER_PRESENT) return;
		LedgerHook.logSummon(mount);
	}

	public static void logRecall(Mount mount, String reason) {
		if (!SummonMounts.LEDGER_PRESENT) return;
		LedgerHook.logRecall(mount, reason);
	}

	public static void logTame(Mount mount) {
		if (!SummonMounts.LEDGER_PRESENT) return;
		LedgerHook.logTame(mount);
	}

	public static void logGear(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) {
		if (!SummonMounts.LEDGER_PRESENT) return;
		LedgerHook.logGear(player, saddled, target, itemId);
	}
}
