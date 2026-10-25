package dev.brodino.summonmounts.integrations.ledger;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.integrations.ModIntegration;
import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class LedgerIntegration extends ModIntegration {

    public LedgerIntegration(ModContainer ledgerMod) {
        super(ledgerMod);
    }

    public void initialize() {
        if (!this.isPresent()) return;
        LedgerHook.initialize();
    }

    public void logSummon(Mount mount) {
        if (this.areLogsDisabled()) return;
        LedgerHook.logSummon(mount);
    }

    public void logRecall(Mount mount, String reason) {
        if (this.areLogsDisabled()) return;
        LedgerHook.logRecall(mount, reason);
    }

    public void logTame(Mount mount) {
        if (this.areLogsDisabled()) return;
        LedgerHook.logTame(mount);
    }

    public void logGear(PlayerEntity player, boolean saddled, LivingEntity target, Identifier itemId) {
        if (this.areLogsDisabled()) return;
        LedgerHook.logGear(player, saddled, target, itemId);
    }

    private boolean areLogsDisabled() {
        return !this.isPresent() || !SummonMounts.CONFIG.getLedgerConfig().logsEnabled;
    }
}
