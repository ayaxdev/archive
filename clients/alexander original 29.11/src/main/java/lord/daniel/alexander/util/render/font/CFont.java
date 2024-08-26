package lord.daniel.alexander.util.render.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.module.impl.options.FontRendererOptionModule;
import lord.daniel.alexander.storage.impl.FontStorage;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.font.modern.SmoothFontRenderer;
import lord.daniel.alexander.util.render.font.old.SharpFontRenderer;
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
        this.awtFont = awtFont;
        this.family = family;
        this.type = type;
        this.size = size;

        this.fontRendererHashMap.put("SmoothedOut", new SmoothFontRenderer(this, awtFont, true));
        this.fontRendererHashMap.put("Old", new SharpFontRenderer(this, awtFont, true));
    }

    private final HashMap<String, CFontRenderer> fontRendererHashMap = new HashMap<>();
    private FontRendererOptionModule fontRendererOptionModule;

    public int drawStringWithShadow(final String text, final float x, final float y, final int color) {
        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        return fontRendererHashMap.get(fontRendererOptionModule.mode.getValue()).drawStringWithShadow(text, x, y, color);
    }

    public void drawCenteredStringWithShadow(final String text, final float x, final float y, final int color) {
        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        fontRendererHashMap.get(fontRendererOptionModule.mode.getValue()).drawCenteredStringWithShadow(text, x, y, color);
    }

    public int drawString(String text, float x, float y, final int color, final boolean shadow) {
        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        return fontRendererHashMap.get(fontRendererOptionModule.mode.getValue()).drawString(text, x, y, color, shadow);
    }

    public int getStringWidth(String text) {
        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        return fontRendererHashMap.get(fontRendererOptionModule.mode.getValue()).getStringWidth(text);
    }

    public float getHeight() {
        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        return fontRendererHashMap.get(fontRendererOptionModule.mode.getValue()).getHeight();
    }


}
