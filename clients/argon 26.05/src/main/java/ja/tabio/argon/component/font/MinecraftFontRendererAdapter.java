package ja.tabio.argon.component.font;

import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.mixin.DrawContextAccessor;
import net.minecraft.client.font.TextRenderer;

public class MinecraftFontRendererAdapter implements FontRenderer, Minecraft {

    public static final MinecraftFontRendererAdapter INSTANCE = new MinecraftFontRendererAdapter();

    @Override
    public int drawString(Renderer2D renderer2D, String text, float x, float y, int color) {
        int i = mc.textRenderer.draw(text, x, y, color, false, renderer2D.drawContext().getMatrices().peek().getPositionMatrix(), renderer2D.drawContext().getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880, false);
        ((DrawContextAccessor) renderer2D.drawContext()).tryDrawI();
        return i;
    }

    @Override
    public float getStringWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }

    @Override
    public float getStringHeight(String text) {
        return mc.textRenderer.fontHeight;
    }
}
