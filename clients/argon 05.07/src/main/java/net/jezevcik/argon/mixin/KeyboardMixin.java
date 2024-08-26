package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.KeyPressEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            final KeyPressEvent keyPressEvent = new KeyPressEvent(key, modifiers, action);
            ParekClient.getInstance().eventBus.post(keyPressEvent);

            if (keyPressEvent.cancelled) {
                ci.cancel();
            }
        }
    }

}