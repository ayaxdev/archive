package ja.tabio.argon.component.font;

import ja.tabio.argon.component.render.Renderer2D;

public interface FontRenderer {

    int drawString(Renderer2D renderer2D, String text, float x, float y, int color);

    float getStringWidth(String text);

    float getStringHeight(String text);

}
