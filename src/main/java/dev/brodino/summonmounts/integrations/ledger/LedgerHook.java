package dev.brodino.summonmounts.integrations.ledger;

import com.github.quiltservertools.ledger.Ledger;
import com.github.quiltservertools.ledger.registry.ActionRegistry;
import dev.brodino.summonmounts.integrations.ledger.actions.MountGearChangeType;
import dev.brodino.summonmounts.integrations.ledger.actions.RecallActionType;
import dev.brodino.summonmounts.integrations.ledger.actions.SummonActionType;
import dev.brodino.summonmounts.integrations.ledger.actions.TamedActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class LedgerHook {

	public static void initialize() {
		ActionRegistry.INSTANCE.registerActionType(SummonActionType::new);
		ActionRegistry.INSTANCE.registerActionType(RecallActionType::new);
		ActionRegistry.INSTANCE.registerActionType(TamedActionType::new);
		ActionRegistry.INSTANCE.registerActionType(MountGearChangeType::new);
	}

	public static void logSummon(Mount mount) { Ledger.getApi().logAction(SummonActionType.create(mount)); }
	public static void logRecall(Mount mount, String reason) { Ledger.getApi().logAction(RecallActionType.create(mount, reason)); }
	public static void logTame(Mount mount) { Ledger.getApi().logAction(TamedActionType.create(mount)); }
	public static void logGear(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) { Ledger.getApi().logAction(MountGearChangeType.create(player, saddled, target, itemId)); }
}
