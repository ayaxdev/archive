package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.LoadingOverlayEvent;
import ja.tabio.argon.event.impl.ReloadingLoadingOverlayEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class LoadingOverlayMixin {

    @Final
    @Shadow
    private boolean reloading;

    @Shadow
    private long reloadCompleteTime;

    @Inject(at = @At("TAIL"), method = "render")
    public void setScreen(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final LoadingOverlayEvent loadingOverlayEvent = new LoadingOverlayEvent(reloadCompleteTime);
        Argon.getInstance().eventBus.post(loadingOverlayEvent);
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;reloading:Z", opcode = Opcodes.GETFIELD))
    private boolean fadeIn(final SplashOverlay instance) {
        final ReloadingLoadingOverlayEvent reloadingLoadingOverlayEvent = new ReloadingLoadingOverlayEvent(reloading);
        Argon.getInstance().eventBus.post(reloadingLoadingOverlayEvent);
        return reloadingLoadingOverlayEvent.reloading;
    }
}