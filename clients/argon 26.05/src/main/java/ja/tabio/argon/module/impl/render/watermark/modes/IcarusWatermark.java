package ja.tabio.argon.module.impl.render.watermark.modes;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.font.FontRenderer;
import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.event.impl.Render2DEvent;
import ja.tabio.argon.font.manager.FontManager;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;

import java.awt.*;

public class IcarusWatermark extends Extension {

    private final FontRenderer icarus = FontManager.FontEntry.builder()
            .family("Pangram")
            .type("Bold")
            .size(80)
            .narrowBy(4)
            .get();

    public IcarusWatermark(String name, Module parent) {
        super(name, parent, false);
    }

    @EventHandler
    public final void on2D(Render2DEvent render2DEvent) {
        final Renderer2D renderer2D = new Renderer2D(render2DEvent.drawContext);

        icarus.drawString(renderer2D, Argon.LATIN_NAME, 8.5f, 0.5f, Color.BLACK.getRGB());
        icarus.drawString(renderer2D, Argon.LATIN_NAME, 8, 0, -1);
    }

    @Override
    public String toString() {
        return name;
    }
}
