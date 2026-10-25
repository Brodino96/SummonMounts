package dev.brodino.summonmounts.ledger;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class LedgerManager {

	public static void initialize(MinecraftServer server) {
		if (areLogsDisabled()) return;
		LedgerHook.initialize();
	}

	public static void logSummon(Mount mount) {
		if (areLogsDisabled()) return;
		LedgerHook.logSummon(mount);
	}

	public static void logRecall(Mount mount, String reason) {
		if (areLogsDisabled()) return;
		LedgerHook.logRecall(mount, reason);
	}

	public static void logTame(Mount mount) {
		if (areLogsDisabled()) return;
		LedgerHook.logTame(mount);
	}

	public static void logGear(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) {
		if (areLogsDisabled()) return;
		LedgerHook.logGear(player, saddled, target, itemId);
	}
	
	private static boolean areLogsDisabled() {
		return !SummonMounts.LEDGER_PRESENT || !SummonMounts.CONFIG.getLedgerConfig().logsEnabled;
	}
}
