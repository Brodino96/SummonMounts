package dev.brodino.summonmounts.client;

import dev.brodino.summonmounts.ParticleRegistry;
import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.client.particle.FeedParticle;
import dev.brodino.summonmounts.network.ForceLandTracker;
import dev.brodino.summonmounts.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class SummonMountsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkManager.registerClientPacketReceivers();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ForceLandTracker.clear());

        ResourceManagerHelper.registerBuiltinResourcePack(
                new Identifier(SummonMounts.MOD_ID, "alternative_ocarinas"),
                FabricLoader.getInstance().getModContainer(SummonMounts.MOD_ID).orElseThrow(),
                "Alternative Ocarinas",
                ResourcePackActivationType.NORMAL
        );
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.FEED_PARTICLE, FeedParticle.Factory::new);
    }
}
