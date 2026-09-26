package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class SummonActionType extends AbstractActionType {

	@Override
	public @NotNull String getIdentifier() { return "mount-summon"; }

	@Override
	public @NotNull String getTranslationType() { return "entity"; }

	@Override
	public @NotNull Text getActionMessage() { return Text.translatable("ledger.summonmounts.summoned"); }

	public static SummonActionType create(Mount mount) {
		SummonActionType action = new SummonActionType();
		action.setSourceProfile(mount.getSummoner().getGameProfile());
		action.setSourceName(mount.getSummoner().getName().getString());
		action.setPos(new BlockPos(mount.getPos()));
		action.setObjectIdentifier(mount.getIdentifier());
		return action;
	}

}
