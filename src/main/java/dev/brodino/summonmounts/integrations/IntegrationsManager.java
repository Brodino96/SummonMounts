package dev.brodino.summonmounts.integrations;

import dev.brodino.summonmounts.integrations.combatlog.CombatlogIntegration;
import dev.brodino.summonmounts.integrations.ledger.LedgerIntegration;
import net.fabricmc.loader.api.FabricLoader;

public class IntegrationsManager {

    public final CombatlogIntegration COMBATLOG;
    public final LedgerIntegration LEDGER;

    public IntegrationsManager() {
        COMBATLOG = new CombatlogIntegration(FabricLoader.getInstance().getModContainer("combatlog").orElse(null));
        LEDGER = new LedgerIntegration(FabricLoader.getInstance().getModContainer("ledger").orElse(null));
    }
}
