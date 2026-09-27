package dev.brodino.summonmounts.mixin.mythicmounts;

import com.yahoo.chirpycricket.mythicmounts.entity.FlyingMountEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition("mythicmounts"))
@Mixin(FlyingMountEntity.class)
public interface FlyingMountEntityInvoker {

    @Invoker(value = "setFlyingParams", remap = false)
    void summonmounts$setFlyingParams(boolean flying);
}
