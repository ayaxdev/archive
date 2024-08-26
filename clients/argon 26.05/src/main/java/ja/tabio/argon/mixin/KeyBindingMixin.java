package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.KeyPressedStateEvent;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void onIsPressed(CallbackInfoReturnable<Boolean> cir) {
        KeyPressedStateEvent keyPressedStateEvent = new KeyPressedStateEvent((KeyBinding) (Object) this, cir.getReturnValueZ());
        Argon.getInstance().eventBus.post(keyPressedStateEvent);
        cir.setReturnValue(keyPressedStateEvent.pressed);
    }
}