package net.brodino.summonmounts;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Config extends ConfigWrapper<net.brodino.summonmounts.ConfigHelper> {

    private final Option<java.lang.String> summonItem = this.optionForKey(new Option.Key("summonItem"));
    private final Option<java.lang.Integer> itemCooldown = this.optionForKey(new Option.Key("itemCooldown"));
    private final Option<java.util.ArrayList<java.lang.String>> allowedSummons = this.optionForKey(new Option.Key("allowedSummons"));
    private final Option<java.util.ArrayList<java.lang.String>> allowedDimensions = this.optionForKey(new Option.Key("allowedDimensions"));
    private final Option<java.lang.Integer> despawnTime = this.optionForKey(new Option.Key("despawnTime"));
    private final Option<net.brodino.summonmounts.ConfigHelper.Locales> locales = this.optionForKey(new Option.Key("locales"));

    private Config() {
        super(net.brodino.summonmounts.ConfigHelper.class);
    }

    public static Config createAndLoad() {
        var wrapper = new Config();
        wrapper.load();
        return wrapper;
    }

    public java.lang.String summonItem() {
        return summonItem.value();
    }

    public void summonItem(java.lang.String value) {
        summonItem.set(value);
    }

    public int itemCooldown() {
        return itemCooldown.value();
    }

    public void itemCooldown(int value) {
        itemCooldown.set(value);
    }

    public java.util.ArrayList<java.lang.String> allowedSummons() {
        return allowedSummons.value();
    }

    public void allowedSummons(java.util.ArrayList<java.lang.String> value) {
        allowedSummons.set(value);
    }

    public java.util.ArrayList<java.lang.String> allowedDimensions() {
        return allowedDimensions.value();
    }

    public void allowedDimensions(java.util.ArrayList<java.lang.String> value) {
        allowedDimensions.set(value);
    }

    public int despawnTime() {
        return despawnTime.value();
    }

    public void despawnTime(int value) {
        despawnTime.set(value);
    }

    public net.brodino.summonmounts.ConfigHelper.Locales locales() {
        return locales.value();
    }

    public void locales(net.brodino.summonmounts.ConfigHelper.Locales value) {
        locales.set(value);
    }




}

