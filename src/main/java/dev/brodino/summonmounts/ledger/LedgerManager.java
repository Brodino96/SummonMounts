package dev.brodino.summonmounts.ledger;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

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

	public static void logSaddle(PlayerEntity player, boolean saddled, LivingEntity target) {
		if (!SummonMounts.LEDGER_PRESENT) return;
		LedgerHook.logSaddle(player, saddled, target);
	}
}
