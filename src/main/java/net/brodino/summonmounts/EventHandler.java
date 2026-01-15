package net.brodino.summonmounts;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EventHandler {

    public static void initialize() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SummonMounts.SERVER = server;
        });



    }
}
