package ja.tabio.argon.font.manager;

import ja.tabio.argon.Argon;
import ja.tabio.argon.font.CFontRenderer;
import ja.tabio.argon.font.glyph.GlyphPage;
import ja.tabio.argon.interfaces.IClientInitializeable;
import ja.tabio.argon.interfaces.IMinecraft;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class CFontManager implements IClientInitializeable, IMinecraft, Argon.IArgonAccess {

    public final List<FontStruct> fontStructs = new LinkedList<>();
    private final List<UnloadedFontStruct> unloadedFontStructs = new LinkedList<>();

    public CFontRenderer get(String name, int size) {
        for (FontStruct fontStruct : fontStructs) {
            if (fontStruct.fontName.equalsIgnoreCase(name) && fontStruct.size == size)
                return fontStruct.renderer;
        }

        try {
            final FontStruct fontStruct = prepare(name, size, true, true).create();
            fontStructs.add(fontStruct);
            return fontStruct.renderer;
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed to create new font {} {}", name, size, e);
            return get("Arial", 19);
        }
    }

    @Override
    public void init() throws IOException, FontFormatException {
        unloadedFontStructs.add(prepare("Arial", 19, true, true));
    }

    @Override
    public void start() {
        for (UnloadedFontStruct unloadedFontStruct : unloadedFontStructs) {
            fontStructs.add(unloadedFontStruct.create());
        }
    }

    private UnloadedFontStruct prepare(final String name, final int size, boolean antiAliasing, boolean fractionalMetrics) throws IOException, FontFormatException {
        final Font regular = getFont(String.format("argon/font/%s/Regular.ttf", name), size, Font.PLAIN);
        final Font italic = getFont(String.format("argon/font/%s/Italic.ttf", name), size, Font.PLAIN);

        final Font bold = getFont(String.format("argon/font/%s/Bold.ttf", name), size, Font.PLAIN);
        final Font boldItalic = getFont(String.format("argon/font/%s/BoldItalic.ttf", name), size, Font.PLAIN);

        final LinkedList<Font> fonts = new LinkedList<>();
        fonts.add(regular);
        fonts.add(bold);
        fonts.add(italic);
        fonts.add(boldItalic);

        return new UnloadedFontStruct(fonts, name, size, antiAliasing, fractionalMetrics);
    }

    private UnloadedFontStruct preparePartial(String name, int size, boolean antiAliasing, boolean fractionalMetrics) throws IOException, FontFormatException {
        final Font regular = getFont(String.format("argon/font/%s/Regular.ttf", name), size, Font.PLAIN);
        final Font bold = getFont(String.format("argon/font/%s/Regular.ttf", name), size, Font.BOLD);
        final Font italic = getFont(String.format("argon/font/%s/Italic.ttf", name), size, Font.PLAIN);
        final Font boldItalic = getFont(String.format("argon/font/%s/Italic.ttf", name), size, Font.BOLD);

        final LinkedList<Font> fonts = new LinkedList<>();
        fonts.add(regular);
        fonts.add(bold);
        fonts.add(italic);
        fonts.add(boldItalic);

        return new UnloadedFontStruct(fonts, name, size, antiAliasing, fractionalMetrics);
    }

    private Font getFont(String path, int size, int type) throws IOException, FontFormatException {
        try(InputStream stream = CFontManager.class.getResourceAsStream("/assets/minecraft/" + path)) {
            if (stream == null)
                throw new NullPointerException("Font stream is null");

            return Font.createFont(type, stream).deriveFont(size);
        }
    }

    public record FontStruct(CFontRenderer renderer, String fontName, int size) { }

    private record UnloadedFontStruct(List<Font> fonts, String name, int size, boolean antiAliasing, boolean fractionalMetrics) {

        public FontStruct create() {
            final GlyphPage regular = new GlyphPage(fonts.get(0), antiAliasing, fractionalMetrics);
            final GlyphPage bold = new GlyphPage(fonts.get(1), antiAliasing, fractionalMetrics);
            final GlyphPage italic = new GlyphPage(fonts.get(2), antiAliasing, fractionalMetrics);
            final GlyphPage boldItalic = new GlyphPage(fonts.get(3), antiAliasing, fractionalMetrics);

            char[] chars = new char[256];

            for (int i = 0; i < chars.length; i++) {
                chars[i] = (char) i;
            }

            regular.generateGlyphPage(chars);
            regular.setupTexture();
            bold.generateGlyphPage(chars);
            bold.setupTexture();
            italic.generateGlyphPage(chars);
            italic.setupTexture();
            boldItalic.generateGlyphPage(chars);
            boldItalic.setupTexture();

            return new FontStruct(new CFontRenderer(regular, bold, italic, boldItalic), name, size);
        }

    }
}
