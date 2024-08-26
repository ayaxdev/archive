package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.*;
import net.jezevcik.argon.renderer.RenderManager;
import net.jezevcik.argon.system.initialize.InitializeStage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.PeriodicNotificationManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Final private PeriodicNotificationManager regionalComplianciesManager;

    @Shadow @Final private long field_46550;

    @Shadow public abstract void tick();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"))
    public void injectPreMinecraft(CallbackInfo callbackInfo) {
        ParekClient.getInstance().init(InitializeStage.PRE_MINECRAFT);
    }

    @Inject(method = "<init>", at = @At(value = "CONSTANT", args = "stringValue=Startup"))
    public void injectPostMinecraft(CallbackInfo callbackInfo) {
        ParekClient.getInstance().init(InitializeStage.POST_MINECRAFT);
    }

    @Inject(method = "getWindowTitle", at = @At("TAIL"), cancellable = true)
    public void injectWindowTitle(CallbackInfoReturnable<String> callbackInfoReturnable) {
        final WindowTitleEvent windowTitleEvent = new WindowTitleEvent(callbackInfoReturnable.getReturnValue());
        ParekClient.getInstance().eventBus.post(windowTitleEvent);
        callbackInfoReturnable.setReturnValue(windowTitleEvent.title);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateCrosshairTarget(F)V", shift = At.Shift.BEFORE))
    public void onPreRaytrace(CallbackInfo ci) {
        ParekClient.getInstance().eventBus.post(new PreTickRaytraceEvent());
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(boolean tick, CallbackInfo ci) {
        RenderManager.drawFrame();
    }

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Screen injectScreenModification(Screen screen) {
        final OpenScreenEvent openScreenEvent = new OpenScreenEvent(screen);
        ParekClient.getInstance().eventBus.post(openScreenEvent);
        return openScreenEvent.screen;
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void injectOpenScreen(Screen screen, CallbackInfo callbackInfo) {
        final OpenScreenEvent openScreenEvent = new OpenScreenEvent(screen);
        ParekClient.getInstance().eventBus.post(openScreenEvent);

        if (openScreenEvent.cancelled)
            callbackInfo.cancel();
    }

    @Inject(method = "stop", at= @At("HEAD"))
    public void injectClientStop(CallbackInfo callbackInfo) {
        ParekClient.getInstance().stop();
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    public void injectClickProcessing(CallbackInfo callbackInfo) {
        ParekClient.getInstance().eventBus.post(new ClickProcessingEvent());
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void injectTickEvent(CallbackInfo callbackInfo) {
        final TickEvent tickEvent = new TickEvent();
        ParekClient.getInstance().eventBus.post(tickEvent);

        if (tickEvent.cancelled)
            callbackInfo.cancel();
    }

    @Inject(method = "handleInputEvents", at = @At(value = "HEAD", shift = At.Shift.BY, by = 2))
    public void injectGui(CallbackInfo callbackInfo) {
        ParekClient.getInstance().eventBus.post(new GuiHandleEvent());
    }

}
