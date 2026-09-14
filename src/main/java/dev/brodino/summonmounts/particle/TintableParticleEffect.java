package dev.brodino.summonmounts.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.Locale;
import java.util.function.BiFunction;

public abstract class TintableParticleEffect extends AbstractDustParticleEffect {
    protected TintableParticleEffect(Vec3f color, float scale) {
        super(color, scale);
    }

    protected static <T extends TintableParticleEffect> Codec<T> createCodec(BiFunction<Vec3f, Float, T> factory) {
        return RecordCodecBuilder.create((instance) -> instance.group(
                Vec3f.CODEC.fieldOf("color").forGetter((effect) -> effect.color),
                Codec.floatRange(0.01F, 4.0F).fieldOf("scale").forGetter((effect) -> effect.scale)
        ).apply(instance, factory));
    }

    protected static <T extends TintableParticleEffect> ParticleEffect.Factory<T> createFactory(BiFunction<Vec3f, Float, T> factory) {
        return new ParticleEffect.Factory<>() {
            public T read(ParticleType<T> type, StringReader reader) throws CommandSyntaxException {
                Vec3f vec3f = AbstractDustParticleEffect.readColor(reader);
                reader.expect(' ');
                float f = reader.readFloat();
                return factory.apply(vec3f, f);
            }

            public T read(ParticleType<T> type, PacketByteBuf buf) {
                return factory.apply(AbstractDustParticleEffect.readColor(buf), buf.readFloat());
            }
        };
    }

    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.color.getX(), this.color.getY(), this.color.getZ(), this.scale);
    }
}
