package net.brodino.summonmounts;

import io.wispforest.owo.config.annotation.Config;

import java.util.ArrayList;

@Config(name = "summonmounts", wrapperName = "Config")
public class ConfigHelper {

    public String summonItem = "minecraft:echo_shard";
    public int itemCooldown = 10;
    public ArrayList<String> allowedSummons = new ArrayList<>();
    public ArrayList<String> allowedDimensions = new ArrayList<>();
    public int despawnTime =  30;

    public Locales locales = new Locales();

    public ConfigHelper() {
        this.allowedSummons.add("minecraft:horse");
        this.allowedSummons.add("minecraft:mule");
        this.allowedSummons.add("minecraft:donkey");

        this.allowedDimensions.add("minecraft:overworld");
    }

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
        }
    }
}