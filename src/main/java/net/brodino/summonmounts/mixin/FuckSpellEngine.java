package net.brodino.summonmounts.mixin;

import net.brodino.summonmounts.AntiAbuse;
import net.brodino.summonmounts.SummonMounts;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class FuckSpellEngine {

    @Inject( method = "onPlayerInteractItem", at = @At("HEAD"), cancellable = true)
    private void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;

        SummonMounts.LOGGER.info("Player is using an item");

        ItemStack stack = player.getStackInHand(packet.getHand());

        if (!AntiAbuse.canHeDoThat(player, stack)) {
            AntiAbuse.notifyPlayer(player);
            ci.cancel();
        }
    }
}
