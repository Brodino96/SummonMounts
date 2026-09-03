package dev.brodino.summonmounts.mixin;

import com.yahoo.chirpycricket.mythicmounts.entity.FlyingMountEntity;
import dev.brodino.summonmounts.MountManager;
import dev.brodino.summonmounts.mount.Mount;
import dev.brodino.summonmounts.mount.RecallReason;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Restriction(require = @Condition("mythicmounts"))
@Mixin(value = FlyingMountEntity.class)
public class FlyingMountEntityMixin {

	@Inject(method = "flyingTravel", at = @At("HEAD"), cancellable = true)
	public void flyingTravel(Vec3d movementInput, CallbackInfo ci) {

		AbstractHorseEntity entity = (AbstractHorseEntity) (Object) this;

		Optional<Mount> mountOptional = MountManager.getMountFromEntity(entity);
		if (mountOptional.isEmpty()) {
			return;
		}

		Mount mount = mountOptional.get();

		if (!mount.shouldBeRecalled()) {
			return;
		}

		if (entity.isOnGround()) {
			MountManager.recall(mount.getSummoner(), RecallReason.AIRBORNE);
			return;
		}

		Vec3d velocity = entity.getVelocity();

		entity.setVelocity(velocity.x,-1, velocity.z);
		entity.move(MovementType.SELF, entity.getVelocity());
		entity.fallDistance = 0;

		ci.cancel();
	}
}
