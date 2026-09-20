package dev.brodino.summonmounts.mixin.mythicmounts;

import com.yahoo.chirpycricket.mythicmounts.entity.FlyingMountEntity;
import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.mount.Mount;
import dev.brodino.summonmounts.mount.RecallReason;
import dev.brodino.summonmounts.network.ForceLandTracker;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Restriction(require = @Condition("mythicmounts"))
@Mixin(value = FlyingMountEntity.class)
public class FlyingMountEntityMixin {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void summonmounts$travel(Vec3d movementInput, CallbackInfo ci) {
        HorseEntity entity = (HorseEntity) (Object) this;
        Optional<Mount> mountOptional = Optional.empty();

        if (entity.getWorld().isClient()) {
            if (!ForceLandTracker.isForceLanding(entity.getUuid())) {
                return;
            }
        } else {
            mountOptional = MountManager.getMountFromEntity(entity);
            if (mountOptional.isEmpty() || !mountOptional.get().shouldBeRecalled()) {
                return;
            }
        }

        ((AbstractHorseEntityAccessor) entity).setJumpStrength(0.0F);
        ((FlyingMountEntityInvoker) entity).summonmounts$setFlyingParams(false);

        if (entity.isOnGround()) {
            mountOptional.ifPresent(mount -> MountManager.recall(mount.getSummoner(), RecallReason.AIRBORNE));
            ci.cancel();
            return;
        }

        Vec3d velocity = entity.getVelocity();
        entity.setVelocity(velocity.x, -1, velocity.z);
        entity.move(MovementType.SELF, entity.getVelocity());
        entity.fallDistance = 0;
        ci.cancel();
    }
}
