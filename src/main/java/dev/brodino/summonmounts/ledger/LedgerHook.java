package dev.brodino.summonmounts.ledger;

import com.github.quiltservertools.ledger.Ledger;
import com.github.quiltservertools.ledger.actions.ActionType;
import com.github.quiltservertools.ledger.api.LedgerApi;
import com.github.quiltservertools.ledger.registry.ActionRegistry;
import dev.brodino.summonmounts.ledger.actions.RecallActionType;
import dev.brodino.summonmounts.ledger.actions.SummonActionType;
import dev.brodino.summonmounts.ledger.actions.TamedActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.util.math.BlockPos;

public class LedgerHook {

	private static final LedgerApi LEDGER_API = Ledger.getApi();

	public static void initialize() {
		ActionRegistry.INSTANCE.registerActionType(SummonActionType::new);
		ActionRegistry.INSTANCE.registerActionType(RecallActionType::new);
		ActionRegistry.INSTANCE.registerActionType(TamedActionType::new);
	}

	public static void logSummon(Mount mount) {
		LEDGER_API.logAction(getAction(mount, new SummonActionType()));
	}

	public static void logRecall(Mount mount, String reason) {
		LEDGER_API.logAction(getAction(mount, new RecallActionType(), reason));
	}

	public static void logTame(Mount mount) {
		LEDGER_API.logAction(getAction(mount, new TamedActionType()));
	}

	private static ActionType getAction(Mount mount, ActionType action) {
		action.setSourceProfile(mount.getSummoner().getGameProfile());
		action.setSourceName(mount.getSummoner().getName().getString());
		action.setPos(new BlockPos(mount.getPos()));
		action.setObjectIdentifier(mount.getIdentifier());
		return action;
	}

	private static ActionType getAction(Mount mount, ActionType action, String extraData) {
		action.setExtraData(extraData);
		return getAction(mount, action);
	}
}
