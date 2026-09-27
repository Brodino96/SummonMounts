package dev.brodino.summonmounts.mixin.mythicmounts;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Restriction(require = @Condition("mythicmounts"))
@Mixin(AbstractHorseEntity.class)
public interface AbstractHorseEntityAccessor {

    @Accessor("jumpStrength")
    void setJumpStrength(float jumpStrength);

    @Accessor("jumpStrength")
    float getJumpStrength();
}