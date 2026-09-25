package dev.brodino.summonmounts.ledger;

import com.github.quiltservertools.ledger.Ledger;
import com.github.quiltservertools.ledger.api.LedgerApi;
import com.github.quiltservertools.ledger.registry.ActionRegistry;
import dev.brodino.summonmounts.ledger.actions.SummonActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class LedgerManager {

	private static final LedgerApi LEDGER_API = FabricLoader.getInstance().isModLoaded("ledger") ? Ledger.getApi() : null;

	public static void initialize(MinecraftServer server) {
		ActionRegistry.INSTANCE.registerActionType(SummonActionType::new);
	}

	public static void logSummon(Mount mount) {
		if (LEDGER_API != null) {
			LEDGER_API.logAction(getSummonAction(mount));
		}
	}

	private static SummonActionType getSummonAction(Mount mount) {
		SummonActionType action = new SummonActionType();
		action.setSourceProfile(mount.getSummoner().getGameProfile());
		action.setPos(new BlockPos(mount.getPos()));
		action.setObjectIdentifier(mount.getIdentifier());
		return action;
	}
}
