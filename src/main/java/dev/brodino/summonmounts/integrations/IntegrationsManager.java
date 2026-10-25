package dev.brodino.summonmounts.integrations;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.integrations.combatlog.CombatlogIntegration;
import dev.brodino.summonmounts.integrations.ledger.LedgerIntegration;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class IntegrationsManager {

    private final CombatlogIntegration COMBATLOG = new CombatlogIntegration(FabricLoader.getInstance().getModContainer("combatlog").orElse(null));
    private final LedgerIntegration LEDGER = new LedgerIntegration(FabricLoader.getInstance().getModContainer("ledger").orElse(null));

    public IntegrationsManager() {

    }

    public boolean isPlayerInCombat(PlayerEntity player) {
        return SummonMounts.CONFIG.getCombatLogConfig().combatBlockSummon && COMBATLOG.isPresent() && COMBATLOG.isInCombat(player);
    }

    public void initializeLedger(MinecraftServer server) {
        LEDGER.initialize();
    }

    public void logSummon(Mount mount) {
        LEDGER.logSummon(mount);
    }

    public void logRecall(Mount mount, String reason) {
        LEDGER.logRecall(mount, reason);
    }

    public void logTame(Mount mount) {
        LEDGER.logTame(mount);
    }

    public void logGear(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) {
        LEDGER.logGear(player, saddled, target, itemId);
    }
}
