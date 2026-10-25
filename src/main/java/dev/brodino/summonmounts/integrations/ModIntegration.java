package dev.brodino.summonmounts.integrations;

import net.fabricmc.loader.api.ModContainer;

public abstract class ModIntegration {

    private final boolean isPresent;
    private final String version;

    public ModIntegration(ModContainer mod) {
        this.isPresent = mod != null;
        this.version = mod != null
                ? mod.getMetadata().getVersion().getFriendlyString()
                : "";
    }

    public boolean isPresent() { return this.isPresent; }
    public String getVersion() { return this.version; }
}
