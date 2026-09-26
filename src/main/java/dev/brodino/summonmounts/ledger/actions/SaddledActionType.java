package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class SaddledActionType extends AbstractActionType {
	@Override
	public @NotNull String getIdentifier() {
		return "";
	}

	@Override
	public @NotNull String getTranslationType() {
		return "";
	}

	public static SaddledActionType getInstance(PlayerEntity player, boolean saddled, LivingEntity target) {
		SaddledActionType action = new SaddledActionType();
		action.setSourceProfile(player.getGameProfile());
		action.setSourceName(player.getName().getString());
		action.setPos(target.getBlockPos());
		action.setObjectIdentifier(Registry.ENTITY_TYPE.getId(target.getType()));
		return action;
	}
}
