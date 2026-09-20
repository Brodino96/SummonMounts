package dev.brodino.summonmounts.mount;

import java.util.Locale;

public enum RecallReason {
    NONE("Recalling {}'s mount"),
    TAMED("Recalling {}'s mount because it just got tame"),
    MANUAL("Recalling {}'s mount because of manual recall"),
    DEATH("Recalling {}'s mount because it died"),
    IDLE("Recalling {}'s mount because it was idle for too long"),
    ALIVE("Recalling {}'s mount because it was alive for too long"),
    AIRBORNE("Recalling {}'s mount because it's stayed airborne for too long"),
    DISCONNECT("Recalling {}'s mount because player disconnected"),
    DIMENSION_CHANGE("Recalling {}'s mount because player changed dimension"),
    SERVER_STOPPING("Recalling {}'s mount because server is stopping"),
    PLAYER_DEATH("Recalling {}'s mount because player died");

    private final String log;
    private final String reason;

    RecallReason(String log) {
        this.log = log;
        this.reason = "feedback.summonmounts.recall." + this.name().toLowerCase(Locale.ROOT);
    }

    public String getLog() { return this.log; }
    public String getReason() { return this.reason; }
}
