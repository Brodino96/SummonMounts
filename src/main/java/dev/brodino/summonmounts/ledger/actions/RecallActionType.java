package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class RecallActionType extends AbstractActionType {

	@Override
	public @NotNull String getIdentifier() { return "mount-recall"; }

	@Override
	public @NotNull String getTranslationType() { return "entity"; }

	@Override
	public @NotNull Text getActionMessage() {
		return Text.translatable("ledger.summonmounts.recall",
			Text.translatable(this.getExtraData() != null ? this.getExtraData() : "")
		);
	}
}
