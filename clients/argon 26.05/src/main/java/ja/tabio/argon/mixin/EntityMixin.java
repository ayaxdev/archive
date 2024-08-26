package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.RotationGetEvent;
import ja.tabio.argon.event.impl.RotationVectorEvent;
import ja.tabio.argon.event.impl.StrafeEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract float getPitch();

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Shadow public abstract void requestTeleportOffset(double offsetX, double offsetY, double offsetZ);

    @Redirect(method = "lerpPosAndRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F"))
    public float getLerpYaw(Entity instance) {
        if (!(instance instanceof ClientPlayerEntity)) {
            return instance.getYaw();
        }
        RotationGetEvent rotationGetEvent = new RotationGetEvent(getYaw(), getPitch());
        Argon.getInstance().eventBus.post(rotationGetEvent);
        return rotationGetEvent.yaw;
    }

    @Redirect(method = "lerpPosAndRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPitch()F"))
    public float getLerpPitch(Entity instance) {
        if (!(instance instanceof ClientPlayerEntity)) {
            return instance.getPitch();
        }
        RotationGetEvent rotationGetEvent = new RotationGetEvent(getYaw(), getPitch());
        Argon.getInstance().eventBus.post(rotationGetEvent);
        return rotationGetEvent.pitch;
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F"))
    public float getStrafeYaw(Entity instance) {
        if (instance instanceof ClientPlayerEntity) {
            StrafeEvent strafeEvent = new StrafeEvent(instance.getYaw());
            Argon.getInstance().eventBus.post(strafeEvent);
            return strafeEvent.yaw;
        }

        return instance.getYaw();
    }

    @Inject(method = "getRotationVec", at = @At("TAIL"), cancellable = true)
    public void injectRotationVec(float tickDelta, CallbackInfoReturnable<Vec3d> callbackInfoReturnable) {
        final RotationVectorEvent rotationVectorEvent = new RotationVectorEvent((Entity) (Object) this, tickDelta, callbackInfoReturnable.getReturnValue());
        Argon.getInstance().eventBus.post(rotationVectorEvent);
        callbackInfoReturnable.setReturnValue(rotationVectorEvent.result);
        callbackInfoReturnable.cancel();
    }

    @Inject(method = "getRotationVector()Lnet/minecraft/util/math/Vec3d;", at = @At("TAIL"), cancellable = true)
    public void injectRotationVector(CallbackInfoReturnable<Vec3d> callbackInfoReturnable) {
        final RotationVectorEvent rotationVectorEvent = new RotationVectorEvent((Entity) (Object) this, 1.0F, callbackInfoReturnable.getReturnValue());
        Argon.getInstance().eventBus.post(rotationVectorEvent);
        callbackInfoReturnable.setReturnValue(rotationVectorEvent.result);
        callbackInfoReturnable.cancel();
    }
}

