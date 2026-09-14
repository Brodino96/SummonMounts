package dev.brodino.summonmounts.client.particle;

import dev.brodino.summonmounts.particle.TintWitchParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TintWitchParticle extends TintableSpellParticle {
    TintWitchParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, TintWitchParticleEffect effect, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, effect.getColor(), effect.getScale(), spriteProvider);
    }

    @Override
    protected int getBrightness(float tint) {
        float f = MathHelper.clamp((this.age + tint) / this.maxAge, 0.0F, 1.0F);
        int i = super.getBrightness(tint);
        int baseLight = 20;
        int blockLight = (i & 255) + baseLight;
        int j = blockLight + (int)(f * 240.0F);
        if (j > 240) {
            j = 240;
        }
        int k = i >> 16 & 255;
        return j | k << 16;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<TintWitchParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(TintWitchParticleEffect effect, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new TintWitchParticle(world, x, y, z, velocityX, velocityY, velocityZ, effect, this.spriteProvider);
        }
    }
}
