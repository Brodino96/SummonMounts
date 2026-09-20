package dev.brodino.summonmounts.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public abstract class TintableSpellParticle extends SpriteBillboardParticle {
    private static final Random RANDOM = Random.create();
    protected final SpriteProvider spriteProvider;

    protected TintableSpellParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Vec3f color, float scale, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.5 - RANDOM.nextDouble(), velocityY, 0.5 - RANDOM.nextDouble());
        this.velocityMultiplier = 0.96F;
        this.gravityStrength = -0.1F;
        this.field_28787 = true;
        this.velocityY *= 0.2;
        if (velocityX == 0.0 && velocityZ == 0.0) {
            this.velocityX *= 0.1;
            this.velocityZ *= 0.1;
        }
        this.spriteProvider = spriteProvider;
        float f = world.random.nextFloat() * 0.5F + 0.35F;
        this.red = color.getX() * f;
        this.green = color.getY() * f;
        this.blue = color.getZ() * f;
        this.scale *= 0.75F * scale;
        this.maxAge = (int) (8.0F / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }


    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        this.setAlpha(MathHelper.lerp(0.05F, this.alpha, 1.0F));
    }
}
