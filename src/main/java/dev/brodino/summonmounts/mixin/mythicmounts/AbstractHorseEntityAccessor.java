package dev.brodino.summonmounts.mixin.mythicmounts;

import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractHorseEntity.class)
public interface AbstractHorseEntityAccessor {

    @Accessor("jumpStrength")
    void setJumpStrength(float jumpStrength);

    @Accessor("jumpStrength")
    float getJumpStrength();
}