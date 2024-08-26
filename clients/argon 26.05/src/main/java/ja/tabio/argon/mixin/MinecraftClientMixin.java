package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Shadow public abstract void setCrashReportSupplier(CrashReport crashReport);

    @Shadow protected abstract void reset(Screen resettingScreen);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"))
    public void initClient(CallbackInfo callbackInfo) {
        Argon.getInstance().init(Argon.Static.args);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockColors;create()Lnet/minecraft/client/color/block/BlockColors;", shift = At.Shift.BEFORE))
    public void startClient(CallbackInfo callbackInfo) {
        Argon.getInstance().start();
    }

    @Inject(method = "getWindowTitle()Ljava/lang/String;", at = @At("TAIL"), cancellable = true)
    public void overrideTitle(CallbackInfoReturnable<String> title) {
        final GameTitleEvent gameTitleEvent = new GameTitleEvent(title.getReturnValue());
        Argon.getInstance().eventBus.post(gameTitleEvent);
        title.setReturnValue(gameTitleEvent.title);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateCrosshairTarget(F)V", shift = At.Shift.BEFORE))
    public void onPreRaytrace(CallbackInfo ci) {
        Argon.getInstance().eventBus.post(new PreTickRaytraceEvent());
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(boolean tick, CallbackInfo ci) {
        Argon.getInstance().renderManager.updateFrame();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void injectStop(CallbackInfo callbackInfo) {
        Argon.getInstance().end();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void injectPreTick(CallbackInfo callbackInfo) {
        Argon.getInstance().eventBus.post(new TickEvent(Stage.PRE));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void injectPostTick(CallbackInfo callbackInfo) {
        Argon.getInstance().eventBus.post(new TickEvent(Stage.POST));
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    public void injectOnHandleInputEvents(CallbackInfo ci) {
        Argon.getInstance().eventBus.post(new HandleInputEventsEvent());
    }

    @ModifyVariable(at = @At("HEAD"), method = "setScreen", ordinal = 0, argsOnly = true)
    public Screen injectOnSetScreen(Screen screen) {
        if (!Argon.getInstance().loaded)
            return screen;

        final SetScreenEvent setScreenEvent = new SetScreenEvent(screen);
        Argon.getInstance().eventBus.post(setScreenEvent);
        return setScreenEvent.screen;
    }


    @ModifyArg(method = "joinWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;reset(Lnet/minecraft/client/gui/screen/Screen;)V", opcode = Opcodes.INVOKEVIRTUAL), index = 0)
    private Screen setLevelUpdateScreenAndTick(Screen screen) {
        final WorldResetScreenEvent worldResetScreenEvent = new WorldResetScreenEvent(screen);
        Argon.getInstance().eventBus.post(worldResetScreenEvent);
        return worldResetScreenEvent.screen;
    }

}
