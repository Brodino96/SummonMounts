package dev.brodino.summonmounts.particle;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;

public class TintWitchParticleEffect extends TintableParticleEffect {

    public static final ParticleEffect.Factory<TintWitchParticleEffect> PARAMETERS_FACTORY = TintableParticleEffect.createFactory(TintWitchParticleEffect::new);

    public TintWitchParticleEffect(Vec3f color, float scale) {
        super(color, scale);
    }

    public ParticleType<TintWitchParticleEffect> getType() {
        return ParticlesManager.WITCH_PARTICLE;
    }
}
