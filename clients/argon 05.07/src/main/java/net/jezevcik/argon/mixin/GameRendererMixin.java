package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.MoveCameraEvent;
import net.jezevcik.argon.event.impl.RaytraceEvent;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;skipGameRender:Z", shift = At.Shift.BEFORE))
    public void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        ParekClient.getInstance().eventBus.post(new MoveCameraEvent(tickDelta));
    }

    @Inject(method = "updateCrosshairTarget", at = @At("HEAD"), cancellable = true)
    public void onUpdateTargetedEntity(float tickDelta, CallbackInfo ci) {
        RaytraceEvent raytraceEvent = new RaytraceEvent(tickDelta);
        ParekClient.getInstance().eventBus.post(raytraceEvent);

        if (raytraceEvent.cancelled)
            ci.cancel();
    }
}
