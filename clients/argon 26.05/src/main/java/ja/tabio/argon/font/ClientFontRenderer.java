package ja.tabio.argon.font;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.font.FontRenderer;
import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.font.glyph.Glyph;
import ja.tabio.argon.font.glyph.GlyphMap;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.utils.math.MathUtils;
import ja.tabio.argon.utils.minecraft.ResourceUtil;
import ja.tabio.argon.utils.render.ColorUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.Closeable;
import java.util.List;

public class ClientFontRenderer implements Closeable, Minecraft, FontRenderer {
    private static final Char2IntArrayMap COLOR_INVOKERS = new Char2IntArrayMap() {{
        put('0', 0x000000);
        put('1', 0x0000AA);
        put('2', 0x00AA00);
        put('3', 0x00AAAA);
        put('4', 0xAA0000);
        put('5', 0xAA00AA);
        put('6', 0xFFAA00);
        put('7', 0xAAAAAA);
        put('8', 0x555555);
        put('9', 0x5555FF);
        put('A', 0x55FF55);
        put('B', 0x55FFFF);
        put('C', 0xFF5555);
        put('D', 0xFF55FF);
        put('E', 0xFFFF55);
        put('F', 0xFFFFFF);
    }};

    private final Object2ObjectMap<Identifier, ObjectList<DrawEntry>> glyphPageCache = new Object2ObjectOpenHashMap<>();
    private final ObjectList<GlyphMap> maps = new ObjectArrayList<>();
    private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap<>();

    private final float fontSizeInPixels;
    private final int charactersPerPage;
    private final int paddingBetweenCharacters;
    private final int narrowBy;

    private Font font;

    private int scale = 0;
    private int lastScale = -1;

    private boolean initialized;

    public ClientFontRenderer(Font font, float fontSizeInPixels, int charactersPerPage, int paddingBetweenCharacters, int narrowBy) {
        this.fontSizeInPixels = fontSizeInPixels;
        this.charactersPerPage = charactersPerPage;
        this.paddingBetweenCharacters = paddingBetweenCharacters;
        this.narrowBy = narrowBy;

        reload(font, fontSizeInPixels);
    }

    private void reload(Font font, float sizePx) {
        if (initialized) throw new IllegalStateException("Double call to init()");
        initialized = true;
        this.lastScale = (int) mc.getWindow().getScaleFactor();
        this.scale = this.lastScale;
        this.font = font.deriveFont(sizePx * this.scale);
    }

    private GlyphMap generateMap(char from, char to) {
        final GlyphMap gm = new GlyphMap(from, to, this.font, ResourceUtil.getRandomIdentifier(), paddingBetweenCharacters, narrowBy);
        maps.add(gm);
        return gm;
    }

    private Glyph generateGlyph(char glyph) {
        for (GlyphMap map : maps) {
            if (map.contains(glyph)) {
                return map.getGlyph(glyph);
            }
        }

        final int base = MathUtils.floorToNearestMultiple(glyph, charactersPerPage);
        final GlyphMap glyphMap = generateMap((char) base, (char) (base + charactersPerPage));

        return glyphMap.getGlyph(glyph);
    }

    private Glyph findGlyph(char glyph) {
        return allGlyphs.computeIfAbsent(glyph, this::generateGlyph);
    }

    public void drawString(MatrixStack stack, String s, double x, double y, int color) {
        drawString(stack, s, x, y, 0.01f, color);
    }

    public void drawString(MatrixStack stack, String s, double x, double y, Color color) {
        drawString(stack, s, x, y, 0.01f, color);
    }

    public void drawString(MatrixStack stack, String s, double x, double y, double z, int color) {
        float r = ((color >> 16) & 0xff)/ 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color) & 0xff) / 255f;
        float a = ((color >> 24) & 0xff) / 255f;
        drawString(stack, s, (float) x, (float) y, (float) z, r, g, b, a);
    }

    public void drawString(MatrixStack stack, String s, double x, double y, double z, Color color) {
        drawString(stack, s, (float) x, (float) y, (float) z, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha());
    }

    public void drawCenteredString(MatrixStack stack, String s, double x, double y, double z, int color) {
        float r = ((color >> 16) & 0xff) / 255f;
        float g = ((color >> 8) & 0xff) / 255f;
        float b = ((color) & 0xff) / 255f;
        float a = ((color >> 24) & 0xff) / 255f;
        drawString(stack, s, (float) (x - getStringWidth(s) / 2f), (float) y, (float) z, r, g, b, a);
    }

    public void drawCenteredString(MatrixStack stack, String s, double x, double y, double z, Color color) {
        drawString(stack, s, (float) (x - getStringWidth(s) / 2f), (float) y, (float) z, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public void drawCenteredString(MatrixStack stack, String s, float x, float y, float z, float r, float g, float b, float a) {
        drawString(stack, s, x - getStringWidth(s) / 2f, y, z, r, g, b, a);
    }

    public void drawString(MatrixStack stack, String s, float x, float y, float z, float r, float g, float b, float a) {
        updateScale();
;
        y -= 2f;

        stack.push();
        stack.translate(x, y, z);
        stack.scale(1f / this.scale, 1f / this.scale, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        final BufferBuilder bb = Tessellator.getInstance().getBuffer();
        final Matrix4f mat = stack.peek().getPositionMatrix();

        char[] charArray = s.toCharArray();
        float xOffset = 0;
        float yOffset = 0;

        float currentRed = r,
                currentGreen = g,
                currentBlue = b;

        boolean updatingColor = false;
        int lines = 0;

        synchronized (glyphPageCache) {
            for (int i = 0; i < charArray.length; i++) {
                final char currentCharacter = charArray[i];

                if (updatingColor) {
                    updatingColor = false;

                    final char upperCase = Character.toUpperCase(currentCharacter);

                    if (COLOR_INVOKERS.containsKey(upperCase)) {
                        int nextColor = COLOR_INVOKERS.get(upperCase);
                        int[] extracted = ColorUtils.Math.extract(nextColor);
                        currentRed = extracted[0] / 255f;
                        currentGreen = extracted[1] / 255f;
                        currentBlue = extracted[2] / 255f;
                    } else if (upperCase == 'R') {
                        currentRed = r;
                        currentGreen = g;
                        currentBlue = b;
                    }

                    continue;
                }

                if (currentCharacter == '§') {
                    updatingColor = true;

                    continue;
                } else if (currentCharacter == '\n') {
                    yOffset += getStringHeight(s.substring(lines, i)) * scale;
                    xOffset = 0;
                    lines = i + 1;

                    continue;
                }

                final Glyph glyph = findGlyph(currentCharacter);

                if(glyph != null) {
                    if (glyph.value() != ' ') {
                        final Identifier texture = glyph.owner().texture;
                        final DrawEntry entry = new DrawEntry(xOffset, yOffset, currentRed, currentGreen, currentBlue, glyph);

                        glyphPageCache.computeIfAbsent(texture, integer -> new ObjectArrayList<>()).add(entry);
                    }

                    xOffset += glyph.width();
                }
            }

            for (Identifier identifier : glyphPageCache.keySet()) {
                RenderSystem.setShaderTexture(0, identifier);
                final List<DrawEntry> drawEntries = glyphPageCache.get(identifier);

                bb.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

                for (DrawEntry object : drawEntries) {
                    final float glyphX = object.posX;
                    final float glyphY = object.posY;
                    final float glyphRed = object.red;
                    final float glyphGreen = object.green;
                    final float glyphBlue = object.blue;

                    final Glyph glyph = object.glyph;
                    final GlyphMap owner = glyph.owner();

                    float glyphWidth = glyph.width();
                    float glyphHeight = glyph.height();

                    float u1 = (float) glyph.u() / owner.width;
                    float v1 = (float) glyph.v() / owner.height;
                    float u2 = (float) (glyph.u() + glyph.width()) / owner.width;
                    float v2 = (float) (glyph.v() + glyph.height()) / owner.height;

                    bb.vertex(mat, glyphX + 0, glyphY + glyphHeight, 0).texture(u1, v2).color(glyphRed, glyphGreen, glyphBlue, a).next();
                    bb.vertex(mat, glyphX + glyphWidth, glyphY + glyphHeight, 0).texture(u2, v2).color(glyphRed, glyphGreen, glyphBlue, a).next();
                    bb.vertex(mat, glyphX + glyphWidth, glyphY + 0, 0).texture(u2, v1).color(glyphRed, glyphGreen, glyphBlue, a).next();
                    bb.vertex(mat, glyphX + 0, glyphY + 0, 0).texture(u1, v1).color(glyphRed, glyphGreen, glyphBlue, a).next();
                }

                BufferRenderer.drawWithGlobalProgram(bb.end());
            }

            glyphPageCache.clear();
        }

        stack.pop();
    }

    @Override
    public int drawString(Renderer2D renderer2D, String text, float x, float y, int color) {
        this.drawString(renderer2D.drawContext().getMatrices(), text, x, y, color);
        return (int) x;
    }

    public float getStringWidth(String text) {
        if (text == null)
            throw new IllegalArgumentException("String parameter mustn't be null!");

        final String stripped = Formatting.strip(text);

        if (stripped == null)
            throw new NullPointerException("There was an error stripping text out of control codes");

        final char[] strippedCharArray = stripped.toCharArray();

        float currentLine = 0;
        float maxPreviousLines = 0;

        for (char character : strippedCharArray) {
            if (character == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }

            final Glyph glyph = findGlyph(character);
            final float w = glyph == null ? 0 : glyph.width();

            currentLine += w / (float) this.scale;
        }

        return Math.max(currentLine, maxPreviousLines);
    }

    public float getStringHeight(String text) {
        if (text == null)
            throw new IllegalArgumentException("String parameter mustn't be null!");

        if (text.isEmpty())
            text = " ";

        final String stripped = Formatting.strip(text);

        if (stripped == null)
            throw new NullPointerException("There was an error stripping text out of control codes");

        final char[] strippedCharArray = stripped.toCharArray();

        float currentLine = 0;
        float previous = 0;

        for (char character : strippedCharArray) {
            if (character == '\n') {
                if (currentLine == 0) {
                    currentLine = findGlyph(' ').height() / (float) this.scale;
                }

                previous += currentLine;
                currentLine = 0;
                continue;
            }

            final Glyph glyph = findGlyph(character);
            final float h = glyph == null ? 0 : glyph.height();

            currentLine = Math.max(h / (float) this.scale, currentLine);
        }

        return currentLine + previous;
    }

    @Override
    public void close() {
        try {
            for (GlyphMap map : maps) {
                map.destroy();
            }

            maps.clear();
            allGlyphs.clear();
            initialized = false;
        } catch (Exception e) {
            Argon.getInstance().logger.error("There was en error closing font renderer", e);
        }
    }

    public float getFontHeight(String str) {
        return getStringHeight(str);
    }

    private void updateScale() {
        int gs = (int) mc.getWindow().getScaleFactor();
        if (gs != this.lastScale) {
            close();
            reload(this.font, this.fontSizeInPixels);
        }
    }

    record DrawEntry(float posX, float posY, float red, float green, float blue, Glyph glyph) { }
}