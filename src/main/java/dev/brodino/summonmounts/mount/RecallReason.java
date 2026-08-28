package dev.brodino.summonmounts.mount;

import java.util.Locale;

public enum RecallReason {
    NONE,
    IDLE,
    ALIVE,
    DEATH,
    MANUAL,
    AIRBORNE,
    DIMENSION,
    DISCONNECT;

    public String reason() { return "recall.summonmounts.feedback." + this.toString().toLowerCase(Locale.ROOT); }

    public static String getLog(RecallReason reason) {
        return switch (reason) {
            case IDLE -> "Recalling {}'s mount because it was idle for too long";
            case ALIVE -> "Recalling {}'s mount because it was alive for too long";
            case DEATH -> "Recalling {}'s mount because it died";
            case MANUAL -> "Recalling {}'s mount because of manual recall";
            case AIRBORNE -> "Recalling {}'s mount because it's stayed airborne for too long";
            case DIMENSION -> "Recalling {}'s mount because player changed dimension";
            case DISCONNECT -> "Recalling {}'s mount because player disconnected";
            default -> "Recalling {}'s mount";
        };
    }
}
