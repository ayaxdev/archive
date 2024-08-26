package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.KeyPressedStateEvent;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeybindingMixin {

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void onIsPressed(CallbackInfoReturnable<Boolean> cir) {
        final KeyPressedStateEvent keyPressedStateEvent = new KeyPressedStateEvent((KeyBinding) (Object) this, cir.getReturnValue());
        ParekClient.getInstance().eventBus.post(keyPressedStateEvent);
        cir.setReturnValue(keyPressedStateEvent.pressed);
    }

}
