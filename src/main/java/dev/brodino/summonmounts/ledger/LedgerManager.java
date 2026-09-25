package dev.brodino.summonmounts.ledger;

import com.github.quiltservertools.ledger.Ledger;
import com.github.quiltservertools.ledger.api.LedgerApi;
import net.fabricmc.loader.api.FabricLoader;

public class LedgerManager {

	private static final LedgerApi LEDGER_API = FabricLoader.getInstance().isModLoaded("ledger") ? Ledger.getApi() : null;

	public static void initialize() {

	}

	public static void logSummon() {
		if (LEDGER_API != null) {
			LEDGER_API.logAction(new SummonActionType());
		}
	}
}
