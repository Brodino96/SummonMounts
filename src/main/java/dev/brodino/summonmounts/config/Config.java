package dev.brodino.summonmounts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Path configPath;
    private ConfigType data;

    public Config(String configName, Logger logger) {
        Path dataDirectory = Path.of("config");

        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            this.configPath = dataDirectory.resolve(configName + ".json");
            this.load();
        } catch (IOException e) {
            logger.error("Failed to load {}.json", configName);
        }
    }

    private void load() throws IOException {
        if (!Files.exists(this.configPath)) {
            this.data = this.getDefaults();
            this.saveToFile();
            return;
        }

        try (Reader reader = Files.newBufferedReader(this.configPath)) {
            this.data = GSON.fromJson(reader, ConfigType.class);
            if (data == null) {
                this.data = this.getDefaults();
                this.saveToFile();
            }
        }
    }

    private ConfigType getDefaults() {
        return new ConfigType();
    }

    public boolean reload() {
        try {
            this.load();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public boolean save() {
        try {
            this.saveToFile();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void saveToFile() throws IOException {
        try (Writer writer = Files.newBufferedWriter(this.configPath)) {
            GSON.toJson(this.data, writer);
        }
    }

    public ConfigType getData() { return this.data; }
    public List<String> getAllowedDimensions() { return this.data.allowedDimensions; }
    public int getOcarinaCooldown() { return this.data.ocarinasCooldownSeconds; }
    public int getMountAliveTicks() { return this.data.mountAliveSeconds * 20; }
    public int getMountIdleTicks() { return this.data.mountIdleSeconds * 20; }
    public int getMountAirborneTicks() { return this.data.mountAirborneSeconds * 20; }
    public Float getFoodRepair(String id) { return this.data.foodRepair.get(id); }
}
