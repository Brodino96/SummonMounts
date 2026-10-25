package dev.brodino.summonmounts.integrations;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.integrations.combatlog.CombatlogIntegration;
import dev.brodino.summonmounts.integrations.ledger.LedgerIntegration;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

public class IntegrationsManager {

    public final CombatlogIntegration COMBATLOG = new CombatlogIntegration(FabricLoader.getInstance().getModContainer("combatlog").orElse(null));
    public final LedgerIntegration LEDGER = new LedgerIntegration(FabricLoader.getInstance().getModContainer("ledger").orElse(null));

    public IntegrationsManager() {
    }

    public boolean isPlayerInCombat(PlayerEntity player) {
        return SummonMounts.CONFIG.getCombatLogConfig().combatBlockSummon && COMBATLOG.isPresent() && COMBATLOG.isInCombat(player);
    }
}
