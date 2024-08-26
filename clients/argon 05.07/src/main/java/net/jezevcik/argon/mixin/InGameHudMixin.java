package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.RenderUiEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        ParekClient.getInstance().eventBus.post(new RenderUiEvent(context, tickDelta));
    }

}
