package net.brodino.summonmounts;

import io.wispforest.owo.config.annotation.Config;

import java.util.ArrayList;

@Config(name = "summonmounts", wrapperName = "Config")
public class ConfigHelper {

    public String summonItem;
    public ArrayList<String> allowedSummons;
    public Integer despawnTime;
    public ArrayList<String> allowedDimensions;

    public ConfigHelper() {
        this.summonItem = "minecraft:echo_shard";

        this.allowedSummons = new ArrayList<>();
        this.allowedSummons.add("minecraft:horse");
        this.allowedSummons.add("minecraft:mule");
        this.allowedSummons.add("minecraft:donkey");

        this.despawnTime = 5;

        this.allowedDimensions = new ArrayList<>();
        this.allowedDimensions.add("minecraft:overworld");
    }
}