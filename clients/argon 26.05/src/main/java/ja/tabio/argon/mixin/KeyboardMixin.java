package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.HandleKeyEvent;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            HandleKeyEvent handleKeyEvent = new HandleKeyEvent(key, modifiers, HandleKeyEvent.KeyAction.get(action));
            Argon.getInstance().eventBus.post(handleKeyEvent);

            if(handleKeyEvent.cancelled)
                info.cancel();
        }
    }

}