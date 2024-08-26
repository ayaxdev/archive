package ja.tabio.argon.utils.render;

import net.minecraft.client.gui.Gui;

public class DrawUtil {

    public static void drawRectRelative(float x, float y, float width, float height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

}
