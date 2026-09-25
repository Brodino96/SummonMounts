package dev.brodino.summonmounts.ledger;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import dev.brodino.summonmounts.SummonMounts;
import org.jetbrains.annotations.NotNull;

public class SummonActionType extends AbstractActionType {

	private final String identifier = SummonMounts.MOD_ID + ":mount_summon";

	@Override
	public @NotNull String getIdentifier() {
		return this.identifier;
	}

	@Override
	public @NotNull String getTranslationType() {
		return "";
	}
}
