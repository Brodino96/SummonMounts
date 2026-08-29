package dev.brodino.summonmounts.mount;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public enum RecallReason {
    NONE(       "none",                     "Recalling {}'s mount"),
    IDLE(       "idle_for_too_long",        "Recalling {}'s mount because it was idle for too long"),
    TAME(       "tamed",                    "Recalling {}'s mount because it just got tame"),
    ALIVE(      "alive_for_too_long",       "Recalling {}'s mount because it was alive for too long"),
    DEATH(      "death",                    "Recalling {}'s mount because it died"),
    MANUAL(     "manual",                   "Recalling {}'s mount because of manual recall"),
    AIRBORNE(   "airborne_for_too_long",    "Recalling {}'s mount because it's stayed airborne for too long"),
    DIMENSION(  "changed_dimension",        "Recalling {}'s mount because player changed dimension"),
    DISCONNECT( "disconnected",             "Recalling {}'s mount because player disconnected");

    private final String log;
    private final MutableText reason;

    RecallReason(String reason, String log) {
        this.log = log;
        this.reason = Text.translatable("recall.summonmounts.feedback." + reason);
    }

    public String getLog() { return this.log; }
    public MutableText getReason() { return this.reason; }
}
