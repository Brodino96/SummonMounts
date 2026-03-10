package net.brodino.summonmounts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private Path configPath;
	private Config.Type data;

	public Config() {
		Path dataDirectory = Path.of("config");

		try {
			if (!Files.exists(dataDirectory)) {
				Files.createDirectories(dataDirectory);
			}
			this.configPath = dataDirectory.resolve(SummonMounts.MOD_ID + ".json");
			this.load();
		} catch (IOException e) {
			SummonMounts.LOGGER.error("Failed to load " + SummonMounts.MOD_ID + ".json");
		}
	}

	private void load() throws IOException {
		if (!Files.exists(this.configPath)) {
			this.data = this.getDefaults();
			this.save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(this.configPath)) {
			this.data = GSON.fromJson(reader, Config.Type.class);
			if (data == null) {
				this.data = this.getDefaults();
				this.save();
			}
		}
	}

	public void reload() {
		try { this.load(); } catch (IOException ignored) {}
	}

	private void save() throws IOException {
		try (Writer writer = Files.newBufferedWriter(this.configPath)) {
			GSON.toJson(this.data, writer);
		}
	}

	private Config.Type getDefaults() {
		return new Config.Type();
	}

	public String getSummonItem() {
		return this.data.summonItem;
	}

	public int getItemCooldown() {
		return this.data.itemCooldown;
	}

	public int getDespawnTime() {
		return this.data.despawnTime;
	}

	public List<String> getAllowedSummons() {
		return this.data.allowedSummons;
	}

	public List<String>  getAllowedDimensions() {
		return this.data.allowedDimensions;
	}

	public Type.Locales getLocales() {
		return this.data.locales;
	}

	private static class Type {
		public String summonItem = "minecraft:echo_shard";

		public int itemCooldown = 10;
		public int despawnTime = 30;

		public List<String> allowedSummons = List.of("minecraft:horse", "minecraft:mule", "minecraft:donkey" );
		public List<String> allowedDimensions = List.of("minecraft:overworld");

		public Locales locales = new Locales();

		public static class Locales {

			public BindingProcess binding = new BindingProcess();
			public SpawnProcess spawn = new SpawnProcess();
			public DismissProcess dismiss = new DismissProcess();
			public ItemUsageProcess itemUse = new ItemUsageProcess();

			public static class BindingProcess {
				public String alreadyBounded = "L'essenza di questa cavalcatura è già legata da qualcos'altro";
				public String notYours = "Puoi legare solamente l'essenza di una cavalcatura a te familiare";
				public String notAllowed = "Questo tipo di creatura non può essere legata";
				public String success = "Creatura legata con successo";
			}

			public static class SpawnProcess {
				public String noSavedData = "Questo oggetto non ha alcuna cavalcatura legata ad esso";
				public String spawnFailed = "Failed to create mount";
				public String success = "Cavalcatura evocata";
			}

			public static class DismissProcess {
				public String success = "La tua cavalcatura è stata richiamata";
			}

			public static class ItemUsageProcess {
				public String wrongDimension = "La tua cavalcatura si rifiuta di essere evocata";
				public String notBounded = "Questo oggetto non è legato ad alcuna cavalcatura";
				public String wrongItem = "Questo oggetto non è legato alla cavalcatura che hai evocato";
				public String inCombat = "Non puoi evocare una cavalcatura durante un combattimento";
				public String whileRiding = "Non puoi usare questo oggetto mentre sei su una cavalcatura";
			}
		}
	}
}
