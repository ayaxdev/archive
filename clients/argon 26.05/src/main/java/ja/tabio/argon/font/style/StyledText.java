package ja.tabio.argon.font.style;

import ja.tabio.argon.component.font.FontRenderer;
import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.utils.render.ColorUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;

public class StyledText {

    private final Type type;
    private final Offset xOffset;
    private final Offset yOffset;
    private final Addon[] addons;

    public StyledText(Type type, Offset xOffset, Offset yOffset, Addon[] addons) {
        this.type = type;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.addons = addons;
    }

    private FontRenderer fontRenderer;
    private Renderer2D renderer2D;

    public void prepare(Renderer2D renderer2D, FontRenderer fontRenderer) {
        this.renderer2D = renderer2D;
        this.fontRenderer = fontRenderer;
    }

    public void drawString(String text, float startX, float startY, int color) {
        float x = switch (xOffset) {
            case NONE -> startX;
            case CENTERED -> startX + fontRenderer.getStringWidth(text) / 2f;
        };

        float y = switch (yOffset) {
            case NONE -> startY;
            case CENTERED -> startY + fontRenderer.getStringHeight(text) / 2f;
        };

        if (ArrayUtils.contains(addons, Addon.SHADOW)) {
            final int[] extracted = ColorUtils.Math.extract(color);
            extracted[0] = (int) (0.25f * extracted[0]);
            extracted[1] = (int) (0.25f * extracted[1]);
            extracted[2] = (int) (0.25f * extracted[2]);

            fontRenderer.drawString(renderer2D, text, x + 1, y + 1, new Color(extracted[0], extracted[1], extracted[2], extracted[3]).getRGB());
        }

        switch (type) {
            case PLAIN -> fontRenderer.drawString(renderer2D, text, x, y, color);
            case ARGON -> renderer2D.drawStringOutlined(fontRenderer, text, x, y, 2, 2, color, Color.BLACK.getRGB());
        }
    }

    public static FontStyleBuilder builder() {
        return new FontStyleBuilder();
    }

    public static final class FontStyleBuilder {
        public Offset xOffset = Offset.NONE;
        public Offset yOffset = Offset.NONE;
        public Addon[] addons = new Addon[0];
        public Type type = Type.PLAIN;

        public FontStyleBuilder xOffset(Offset offset) {
            this.xOffset = offset;
            return this;
        }

        public FontStyleBuilder yOffset(Offset offset) {
            this.yOffset = offset;
            return this;
        }

        public FontStyleBuilder addons(Addon... addons) {
            this.addons = addons;
            return this;
        }

        public FontStyleBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public StyledText build() {
            return new StyledText(type, xOffset, yOffset, addons);
        }

    }

    public enum Offset {
        NONE, CENTERED;
    }

    public enum Addon {
        SHADOW
    }

    public enum Type {
        PLAIN, ARGON
    }

}
