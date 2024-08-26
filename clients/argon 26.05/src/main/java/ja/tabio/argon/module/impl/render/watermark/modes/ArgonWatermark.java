package ja.tabio.argon.module.impl.render.watermark.modes;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.font.MinecraftFontRendererAdapter;
import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.event.impl.Render2DEvent;
import ja.tabio.argon.font.style.StyledText;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.setting.impl.ModeSetting;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ArgonWatermark extends Extension {

    private final StyledText argonStyle = StyledText.builder().type(StyledText.Type.ARGON).build();

    public final ModeSetting textMode = new ModeSetting("Text", "Latin", "Latin", "Cyrillic");

    public ArgonWatermark(String name, Module parent) {
        super(name, parent, false);
    }

    @EventHandler
    public final void on2D(Render2DEvent render2DEvent) {
        final Renderer2D renderer2D = new Renderer2D(render2DEvent.drawContext);

        final String argonText = this.textMode.getValue().equals("Latin") ? Argon.LATIN_NAME : Argon.CYRILLIC_NAME;
        final String name = argonText.charAt(0) + Formatting.WHITE.toString() + argonText.substring(1);
        final String text = String.format("%s v%s | %s FPS | %s | %s", name, Argon.VERSION,
                mc.getCurrentFps(), mc.getSession().getUsername(), mc.getCurrentServerEntry() == null ? "SinglePlayer" : mc.getCurrentServerEntry().address);
        final float x = 9, y = 8f;

        renderer2D.drawRectAbsolute(x, y, x + 1, y + 6.5f + mc.textRenderer.fontHeight, Color.black.getRGB());
        renderer2D.drawRectAbsolute(x + 5 + mc.textRenderer.getWidth(text), y, x + 5 + mc.textRenderer.getWidth(text) + 1, y + 6.5f + mc.textRenderer.fontHeight, Color.black.getRGB());

        renderer2D.drawRectAbsolute(x, y, x + 5 + mc.textRenderer.getWidth(text) + 1, y + 1, Color.black.getRGB());
        renderer2D.drawRectAbsolute(x, y + 5.5f + mc.textRenderer.fontHeight, x + 5 + mc.textRenderer.getWidth(text) + 1, y + 6.5f + mc.textRenderer.fontHeight, Color.black.getRGB());

        renderer2D.drawRectAbsolute(x + 1, y + 1, x + 5 + mc.textRenderer.getWidth(text), y + 2, new Color(51, 153, 255).getRGB());
        renderer2D.drawRectAbsolute(x + 1, y + 2, x + 5 + mc.textRenderer.getWidth(text), y + 5.5f + mc.textRenderer.fontHeight, new Color(0, 0, 0, 140).getRGB());

        argonStyle.prepare(renderer2D, MinecraftFontRendererAdapter.INSTANCE);
        argonStyle.drawString(text, 13, 13,  new Color(51, 153, 255).getRGB());
    }

    @Override
    public String toString() {
        return name;
    }
}
