package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
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

	public static RecallActionType create(Mount mount, String reason) {
		RecallActionType action = new RecallActionType();
		action.setSourceProfile(mount.getSummoner().getGameProfile());
		action.setSourceName(mount.getSummoner().getName().getString());
		action.setPos(new BlockPos(mount.getPos()));
		action.setObjectIdentifier(mount.getIdentifier());
		action.setExtraData(reason);
		return action;
	}
}
