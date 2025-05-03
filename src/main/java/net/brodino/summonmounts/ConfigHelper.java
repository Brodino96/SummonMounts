package net.brodino.summonmounts;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "summonmounts", wrapperName = "Config")
public class ConfigHelper {

    public String summonItem;
    public String[] allowedSummons;

    public ConfigHelper() {
        this.summonItem = "minecraft:dragon_egg";
        this.allowedSummons = new String[]{"minecraft:horse", "minecraft:mule", "minecraft:donkey"};
    }
}