package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import org.jetbrains.annotations.NotNull;

public class SummonActionType extends AbstractActionType {

	private final String identifier = "mount-summon";

	@Override
	public @NotNull String getIdentifier() { return this.identifier; }

	@Override
	public @NotNull String getTranslationType() { return "entity"; }

}
