package net.brodino.summonmounts.config;

import java.util.ArrayList;

public class ConfigType {

    public boolean debugMode = false;
    public String summonItem = "";

    public boolean particlesOnSummon = true;
    public boolean particlesOnDismiss = true;

    public ArrayList<String> allowedMounts = new ArrayList<>();
    public ArrayList<String> allowedDimensions = new ArrayList<>();
}
