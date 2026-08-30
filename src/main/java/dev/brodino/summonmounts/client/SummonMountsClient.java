package dev.brodino.summonmounts.client;

import dev.brodino.summonmounts.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;

public class SummonMountsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkManager.registerClientPacketReceivers();
    }
}
