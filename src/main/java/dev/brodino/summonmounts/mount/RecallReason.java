package dev.brodino.summonmounts.mount;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Locale;

public enum RecallReason {
    NONE("Recalling {}'s mount"),
    IDLE("Recalling {}'s mount because it was idle for too long"),
    TAME("Recalling {}'s mount because it just got tame"),
    ALIVE("Recalling {}'s mount because it was alive for too long"),
    DEATH("Recalling {}'s mount because it died"),
    MANUAL("Recalling {}'s mount because of manual recall"),
    AIRBORNE("Recalling {}'s mount because it's stayed airborne for too long"),
    DIMENSION("Recalling {}'s mount because player changed dimension"),
    DISCONNECT("Recalling {}'s mount because player disconnected");

    private final String log;
    private final MutableText reason;

    RecallReason(String log) {
        this.log = log;
        this.reason = Text.translatable("recall.summonmounts.feedback." + this.toString().toLowerCase(Locale.ROOT));
    }

    public String getLog() { return this.log; }
    public MutableText getReason() { return this.reason; }
}
