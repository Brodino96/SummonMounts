package dev.brodino.summonmounts.integrations.combatlog;

import dev.brodino.summonmounts.integrations.ModIntegration;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.entity.player.PlayerEntity;

public class CombatlogIntegration extends ModIntegration {

    private final String TagData_Class = this.getTagData_Class();
    private final String IEntityDataSaver_Class = this.getIEntityDataSaver_Class();

    public CombatlogIntegration(ModContainer combatLogMod) {
        super(combatLogMod);
    }

    private String getTagData_Class() {
        return switch (this.getVersion()) {
            case "2.0", "2.2a", "2.3" -> "fabric.me.toastymop.combatlog.util.TagData";
            case "2.6" -> "me.toastymop.combatlog.util.TagData";
            default -> "";
        };
    }

    private String getIEntityDataSaver_Class() {
        return switch (this.getVersion()) {
            case "2.0", "2.2a", "2.3" -> "fabric.me.toastymop.combatlog.util.IEntityDataSaver";
            case "2.6" -> "me.toastymop.combatlog.util.IEntityDataSaver";
            default -> "";
        };
    }

    public boolean isInCombat(PlayerEntity player) {
        if (!this.isPresent()) return false;
        try {

            final ClassLoader loader = CombatlogIntegration.class.getClassLoader();

            final Class<?> tagDataClass = Class.forName(TagData_Class, true, loader);
            final Class<?> entityDataSaverClass = Class.forName(IEntityDataSaver_Class, true, loader);

            if (!entityDataSaverClass.isInstance(player)) {
                throw new IllegalStateException("Player does not implement IEntityDataSaver from CombatLog" + this.getVersion());
            }

            return (boolean) tagDataClass.getMethod("getCombat", entityDataSaverClass).invoke(null, player);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not access Combatlog " + this.getVersion(), e);
        }
    }
}
