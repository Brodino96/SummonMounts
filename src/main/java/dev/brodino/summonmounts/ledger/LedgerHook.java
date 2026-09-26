package dev.brodino.summonmounts.ledger;

import com.github.quiltservertools.ledger.Ledger;
import com.github.quiltservertools.ledger.api.LedgerApi;
import com.github.quiltservertools.ledger.registry.ActionRegistry;
import dev.brodino.summonmounts.ledger.actions.MountGearChangeType;
import dev.brodino.summonmounts.ledger.actions.RecallActionType;
import dev.brodino.summonmounts.ledger.actions.SummonActionType;
import dev.brodino.summonmounts.ledger.actions.TamedActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class LedgerHook {

	private static final LedgerApi LEDGER_API = Ledger.getApi();

	public static void initialize() {
		ActionRegistry.INSTANCE.registerActionType(SummonActionType::new);
		ActionRegistry.INSTANCE.registerActionType(RecallActionType::new);
		ActionRegistry.INSTANCE.registerActionType(TamedActionType::new);
		ActionRegistry.INSTANCE.registerActionType(MountGearChangeType::new);
	}

	public static void logSummon(Mount mount) { LEDGER_API.logAction(SummonActionType.create(mount)); }
	public static void logRecall(Mount mount, String reason) { LEDGER_API.logAction(RecallActionType.create(mount, reason)); }
	public static void logTame(Mount mount) { LEDGER_API.logAction(TamedActionType.create(mount)); }
	public static void logGear(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) { LEDGER_API.logAction(MountGearChangeType.create(player, saddled, target, itemId)); }
}
