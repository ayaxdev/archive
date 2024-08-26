package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.RenderTextEvent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @ModifyVariable(method = "drawLayer(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)F", at = @At("HEAD"), index = 1, argsOnly = true)
    public String injectTextOverride(String text) {
        final RenderTextEvent renderTextEvent = new RenderTextEvent(text);
        ParekClient.getInstance().eventBus.post(renderTextEvent);
        return (String) renderTextEvent.text;
    }

    @ModifyVariable(method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)F", at = @At("HEAD"), index = 1, argsOnly = true)
    public OrderedText injectTextOverride(OrderedText text) {
        final RenderTextEvent renderTextEvent = new RenderTextEvent(text);
        ParekClient.getInstance().eventBus.post(renderTextEvent);
        return (OrderedText) renderTextEvent.text;
    }
}
