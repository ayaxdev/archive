package ja.tabio.argon.module.impl.render.watermark.modes;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.colors.ColorPalette;
import ja.tabio.argon.component.font.FontRenderer;
import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.event.impl.Render2DEvent;
import ja.tabio.argon.font.manager.FontManager;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;

import java.awt.*;
import java.util.Objects;

public class FatalityWatermark extends Extension {

    private final FontRenderer fatality = FontManager.FontEntry.builder()
            .family("Roboto")
            .type("Regular")
            .size(15)
            .get();

    public FatalityWatermark(String name, Module parent) {
        super(name, parent, false);
    }

    @EventHandler
    public final void on2D(Render2DEvent render2DEvent) {
        final Renderer2D renderer2D = new Renderer2D(render2DEvent.drawContext);
        final String text = String.format("$$$ %s.vip $$$ | %s | %s", Argon.LATIN_NAME.toLowerCase(), "Unlicensed",
                mc.isInSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(mc.getCurrentServerEntry()).address);

        final float width2 = fatality.getStringWidth(text) + 8f;
        final int height2 = 20;
        final float posX2 = 2;
        final float posY1 = 2;

        renderer2D.drawRectAbsolute(posX2, posY1, posX2 + width2 + 2.0f, posY1 + height2, new Color(5, 5, 5, 255).getRGB());
        renderer2D.drawBorderedRect(posX2 + 0.5f, posY1 + 0.5f, posX2 + width2 + 1.5f, posY1 + height2 - 0.5f, 0.5f, new Color(40, 40, 40, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
        renderer2D.drawBorderedRect(posX2 + 2, posY1 + 2, posX2 + width2, posY1 + height2 - 2, 0.5f, new Color(22, 22, 22, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
        renderer2D.drawRectAbsolute(posX2 + 2.5f, posY1 + 2.5f, posX2 + width2 - 0.5f, posY1 + 4.5f, new Color(9, 9, 9, 255).getRGB());
        renderer2D.drawGradientLR(posX2 + 2.0f, posY1 + 3, posX2 + 2.0f + width2 - 2, posY1 + 3 + 1, ColorPalette.FATALITY_FIRST.color, ColorPalette.FATALITY_SECOND.color);

        fatality.drawString(renderer2D, text,  posX2 + 5.5F, posY1 + 8.0f, Color.white.getRGB());
    }

    @Override
    public String toString() {
        return name;
    }
}
