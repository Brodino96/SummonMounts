package dev.brodino.summonmounts.mixin.ledger;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseScreenHandler.class)
public class HorseScreenHandlerMixin implements HorseScreenHandlerAccessor {

    @Final
    @Shadow
    private AbstractHorseEntity entity;

    @Override
    public AbstractHorseEntity getEntity() { return this.entity; }
}
