package net.brodino.summonmounts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.brodino.summonmounts.SummonMounts;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Path configPath;
    private ConfigType data;

    public Config() {
        Path dataDirectory = Path.of("config");

        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            this.configPath = dataDirectory.resolve("summonmounts.json");
            this.load();
        } catch (IOException e) {
            SummonMounts.LOGGER.error("Failed to load config.json!", e);
        }
    }

    private void load() throws IOException {
        if (!Files.exists(this.configPath)) {
            this.data = this.getDefaults();
            this.save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(this.configPath)) {
            this.data = GSON.fromJson(reader, ConfigType.class);
            if (data == null) {
                this.data = this.getDefaults();
                this.save();
            }
        }
    }

    public void reload() throws IOException {
        this.load();
    }

    private void save() throws IOException {
        try (Writer writer = Files.newBufferedWriter(this.configPath)) {
            GSON.toJson(this.data, writer);
        }
    }

    private ConfigType getDefaults() {
        return new ConfigType();
    }

    // Getters
    public boolean getDebugMode() {
        return this.data.debugMode;
    }

    public String getSummonItem() {
        return this.data.summonItem;
    }

    public ArrayList<String> getAllowedMounts() {
        return this.data.allowedMounts;
    }

    public ArrayList<String> getAllowedDimensions() {
        return this.data.allowedDimensions;
    }
}
