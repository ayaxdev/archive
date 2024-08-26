package ja.tabio.argon.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.Render2DEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        client.getProfiler().push(Argon.MOD_ID + "_render_2d");

        Argon.getInstance().eventBus.post(new Render2DEvent(context, context.getScaledWindowWidth(), context.getScaledWindowWidth(), tickDelta));

        RenderSystem.applyModelViewMatrix();

        client.getProfiler().pop();
    }


}
