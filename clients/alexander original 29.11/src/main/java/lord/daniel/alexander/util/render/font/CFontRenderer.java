package lord.daniel.alexander.util.render.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

/**
 * Written by Daniel. on 17/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@RequiredArgsConstructor
public abstract class CFontRenderer extends FontRenderer {

    private final CFont cFont;

    @Override
    public int drawStringWithShadow(final String text, final float x, final float y, final int color) {
        drawString(text, x + cFont.getFontRendererOptionModule().xShadowOffset.getValue(), y + cFont.getFontRendererOptionModule().yShadowOffset.getValue(), color, true);
        return drawString(text, x, y, color, false);
    }

    public void drawCenteredStringWithShadow(final String text, final float x, final float y, final int color) {
        drawString(text, x - (getStringWidth(text) >> 1) + cFont.getFontRendererOptionModule().xShadowOffset.getValue(), y + cFont.getFontRendererOptionModule().yShadowOffset.getValue(), new Color(color, true).getRGB(), true);
        drawString(text, x - (getStringWidth(text) >> 1), y, color, false);
    }

    @Override
    public abstract int drawString(String text, float x, float y, final int color, final boolean shadow);

    @Override
    public abstract int getStringWidth(String text);

    public abstract float getHeight();

}
