package dev.brodino.summonmounts.ledger.actions;

import com.github.quiltservertools.ledger.actions.AbstractActionType;
import com.github.quiltservertools.ledger.utility.Sources;
import com.github.quiltservertools.ledger.utility.TextColorPallet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class MountGearChangeType extends AbstractActionType {

	@Override
	public @NotNull String getIdentifier() { return "mount-gear"; }

	@Override
	public @NotNull String getTranslationType() { return "entity"; }

	@Override
	public @NotNull Text getActionMessage() {
        Item item = Registry.ITEM.get(this.getOldObjectIdentifier());
		Text itemText = Text.empty()
			.append(item.getName())
			.setStyle(Style.EMPTY
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(new ItemStack(item))))
					.withColor(TextColorPallet.INSTANCE.getSecondaryVariant().getColor())
			);

		return Boolean.parseBoolean(this.getExtraData())
				? Text.translatable("ledger.summonmounts.gear.add", itemText)
				: Text.translatable("ledger.summonmounts.gear.remove", itemText);

	}

	public static MountGearChangeType create(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) {
		MountGearChangeType action = new MountGearChangeType();
		action.setSourceProfile(player.getGameProfile());
		action.setSourceName(Sources.PLAYER);
		action.setPos(target.getBlockPos());
		action.setOldObjectIdentifier(itemId);
		action.setObjectIdentifier(Registry.ENTITY_TYPE.getId(target.getType()));
		action.setExtraData(String.valueOf(saddled));
		return action;
	}
}
