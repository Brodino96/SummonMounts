package dev.brodino.summonmounts.mixin.mythicmounts;

import com.yahoo.chirpycricket.mythicmounts.entity.FlyingMountEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlyingMountEntity.class)
public interface FlyingMountEntityInvoker {

    @Invoker("setFlyingParams")
    void summonmounts$setFlyingParams(boolean flying);
}
