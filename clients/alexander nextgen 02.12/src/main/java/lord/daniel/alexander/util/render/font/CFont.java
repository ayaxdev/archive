package lord.daniel.alexander.util.render.font;

import lombok.Getter;
import lord.daniel.alexander.util.render.font.impl.SmoothFontRenderer;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.HashMap;

/**
 * Written by Daniel. on 17/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
public class CFont extends FontRenderer {

    private final Font awtFont;
    private final String family, type;
    private final float size;

    public CFont(Font awtFont, String family, String type, float size) {
        super();
        this.awtFont = awtFont;
        this.family = family;
        this.type = type;
        this.size = size;

        this.fontRendererHashMap.put("Normal", new SmoothFontRenderer(this, awtFont, true));
    }

    private final HashMap<String, CFontRenderer> fontRendererHashMap = new HashMap<>();

    public CFontRenderer getFontRenderer() {
        return fontRendererHashMap.get("Normal");
    }


}
