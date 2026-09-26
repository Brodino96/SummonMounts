package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class TamedActionType extends AbstractActionType {

	public static TamedActionType create(Mount mount) {
		TamedActionType action = new TamedActionType();
		action.setSourceProfile(mount.getSummoner().getGameProfile());
		action.setSourceName(mount.getSummoner().getName().getString());
		action.setPos(new BlockPos(mount.getPos()));
		action.setObjectIdentifier(mount.getIdentifier());
		return action;
	}

	@Override
	public @NotNull String getIdentifier() { return "mount-tamed"; }

	@Override
	public @NotNull String getTranslationType() { return "entity"; }

	@Override
	public @NotNull Text getActionMessage() { return Text.translatable("ledger.summonmounts.tamed"); }
}
