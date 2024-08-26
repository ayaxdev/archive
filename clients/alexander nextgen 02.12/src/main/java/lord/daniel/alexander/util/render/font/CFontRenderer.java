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

    protected final CFont cFont;

    @Override
    public int drawStringWithShadow(final String text, final float x, final float y, final int color) {
        drawString(text, x + 0.5f, y + 0.5f, color, true);
        return drawString(text, x, y, color, false);
    }

    public int drawCenteredStringWithShadow(final String text, final float x, final float y, final int color) {
        return drawStringWithShadow(text, x - (getStringWidth(text) >> 1), y, color);
    }

    public int drawCenteredString(final String text, final float x, final float y, final int color) {
        return drawString(text, (int) (x - (getStringWidth(text) >> 1)), (int) y, color);
    }

    public int drawString(String text, float x, float y, final int color) {
        // Fixed for fonts with weird char sizes
        if(cFont.getFamily().equalsIgnoreCase("poppins")) {
            y += getHeight() / 8;
        }

        return drawString(text, x, y, color, false);
    }

    public int drawString(String text, int x, int y, final int color) {
        return drawString(text, (float) x, (float) y, color);
    }

    @Override
    public abstract int drawString(String text, float x, float y, final int color, final boolean shadow);

    @Override
    public abstract int getStringWidth(String text);

    public abstract float getHeight();

}
