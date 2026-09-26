package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class SaddledActionType extends AbstractActionType {

	@Override
	public @NotNull String getIdentifier() { return "mount-saddled"; }

	@Override
	public @NotNull String getTranslationType() { return "entity"; }

	public static SaddledActionType create(PlayerEntity player, boolean saddled, LivingEntity target) {
		SaddledActionType action = new SaddledActionType();
		action.setSourceProfile(player.getGameProfile());
		action.setSourceName(player.getName().getString());
		action.setPos(target.getBlockPos());
		action.setObjectIdentifier(Registry.ENTITY_TYPE.getId(target.getType()));
		action.setExtraData(String.valueOf(saddled));
		return action;
	}

	@Override
	public @NotNull Text getActionMessage() {
		return Boolean.parseBoolean(this.getExtraData())
				? Text.translatable("ledger.summonmounts.saddled")
				: Text.translatable("ledger.summonmounts.unsaddled");
	}
}
