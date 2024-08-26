package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.ToastEvent;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class ToastManagerMixin {

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void injectOnAdd(Toast toast, CallbackInfo callbackInfo) {
        final ToastEvent toastEvent = new ToastEvent(toast);
        ParekClient.getInstance().eventBus.post(toastEvent);

        if (toastEvent.cancelled)
            callbackInfo.cancel();
    }

}
