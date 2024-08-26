package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.StrafeInputEvent;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift = At.Shift.AFTER))
    public void onTick(boolean slowDown, float f, CallbackInfo ci) {
        final StrafeInputEvent strafeInputEvent = new StrafeInputEvent((int) movementForward, (int) movementSideways, jumping, sneaking);
        ParekClient.getInstance().eventBus.post(strafeInputEvent);

        movementForward = strafeInputEvent.moveForward;
        movementSideways = strafeInputEvent.moveSideways;
        jumping = strafeInputEvent.jumping;
        sneaking = strafeInputEvent.sneaking;
    }
}