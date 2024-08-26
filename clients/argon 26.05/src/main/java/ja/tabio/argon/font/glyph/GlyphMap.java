package ja.tabio.argon.font.glyph;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import ja.tabio.argon.Argon;
import ja.tabio.argon.mixin.NativeImageAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GlyphMap {
    private final Char2ObjectArrayMap<Glyph> glyphs = new Char2ObjectArrayMap<>();

    private final char start, end;
    private final Font font;

    public final Identifier texture;

    private final int narrowBy;

    private final int pixelPadding;
    public int width, height;

    private boolean generated = false;

    public GlyphMap(char start, char end, Font font, Identifier identifier, int padding, int narrowBy) {
        this.start = start;
        this.end = end;
        this.font = font;
        this.texture = identifier;
        this.pixelPadding = padding;
        this.narrowBy = narrowBy;
    }

    public Glyph getGlyph(char c) {
        if (!generated) {
            generate();
        }

        return glyphs.get(c);
    }

    public void destroy() {
        MinecraftClient.getInstance().getTextureManager().destroyTexture(this.texture);

        this.glyphs.clear();
        this.width = -1;
        this.height = -1;

        generated = false;
    }

    public boolean contains(char c) {
        return c >= start && c < end;
    }

    private Font getFontForGlyph(char c) {
        font.canDisplay(c);

        return this.font;
    }

    public void generate() {
        if (generated) {
            return;
        }

        final int range = end - start - 1;
        final int charsVert = (int) (Math.ceil(Math.sqrt(range)) * 1.5);  // double as many chars wide as high

        glyphs.clear();

        int generatedCharacters = 0;
        int characterX = 0;
        int maxX = 0, maxY = 0;
        int currentX = 0, currentY = 0;
        int currentRowMaxY = 0;

        final List<Glyph> generatedGlyphs = new ArrayList<>();
        final AffineTransform transformer = new AffineTransform();
        final FontRenderContext context = new FontRenderContext(transformer, true, false);

        while (generatedCharacters <= range) {
            final char currentCharacter = (char) (start + generatedCharacters);
            final Font font = getFontForGlyph(currentCharacter);
            final Rectangle2D stringBounds = font.getStringBounds(String.valueOf(currentCharacter), context);

            final int width = stringBounds.getBounds().width - narrowBy;
            final int height = stringBounds.getBounds().height;
            generatedCharacters++;

            maxX = Math.max(maxX, currentX + width);
            maxY = Math.max(maxY, currentY + height);

            if (characterX >= charsVert) {
                currentX = 0;
                currentY += currentRowMaxY + pixelPadding;
                characterX = 0;
                currentRowMaxY = 0;
            }

            generatedGlyphs.add(new Glyph(currentX, currentY, width, height, currentCharacter, this));

            currentRowMaxY = Math.max(currentRowMaxY, height);
            currentX += width + pixelPadding;
            characterX++;
        }

        final BufferedImage glyphImage = new BufferedImage(Math.max(maxX + pixelPadding, 1), Math.max(maxY + pixelPadding, 1), BufferedImage.TYPE_INT_ARGB);
        width = glyphImage.getWidth();
        height = glyphImage.getHeight();

        Graphics2D graphics = glyphImage.createGraphics();
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.WHITE);

        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (Glyph glyph : generatedGlyphs) {
            graphics.setFont(getFontForGlyph(glyph.value()));

            final FontMetrics fontMetrics = graphics.getFontMetrics();
            graphics.drawString(String.valueOf(glyph.value()), glyph.u(), glyph.v() + fontMetrics.getAscent());

            glyphs.put(glyph.value(), glyph);
        }

        registerBufferedImageTexture(texture, glyphImage);

        generated = true;
    }

    public static void registerBufferedImageTexture(Identifier i, BufferedImage bi) {
        try {
            // argb from BufferedImage is little endian, alpha is actually where the `a` is in the label
            // rgba from NativeImage (and by extension opengl) is big endian, alpha is on the other side (abgr)
            // thank you opengl
            int ow = bi.getWidth();
            int oh = bi.getHeight();
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, ow, oh, false);
            @SuppressWarnings("DataFlowIssue") long ptr = ((NativeImageAccessor) (Object) image).getPointer();
            IntBuffer backingBuffer = MemoryUtil.memIntBuffer(ptr, image.getWidth() * image.getHeight());
            int off = 0;
            Object _d;
            WritableRaster _ra = bi.getRaster();
            ColorModel _cm = bi.getColorModel();
            int nbands = _ra.getNumBands();
            int dataType = _ra.getDataBuffer().getDataType();
            _d = switch (dataType) {
                case DataBuffer.TYPE_BYTE -> new byte[nbands];
                case DataBuffer.TYPE_USHORT -> new short[nbands];
                case DataBuffer.TYPE_INT -> new int[nbands];
                case DataBuffer.TYPE_FLOAT -> new float[nbands];
                case DataBuffer.TYPE_DOUBLE -> new double[nbands];
                default -> throw new IllegalArgumentException("Unknown data buffer type: " +
                        dataType);
            };

            for (int y = 0; y < oh; y++) {
                for (int x = 0; x < ow; x++) {
                    _ra.getDataElements(x, y, _d);
                    int a = _cm.getAlpha(_d);
                    int r = _cm.getRed(_d);
                    int g = _cm.getGreen(_d);
                    int b = _cm.getBlue(_d);
                    int abgr = a << 24 | b << 16 | g << 8 | r;
                    backingBuffer.put(abgr);
                }
            }
            NativeImageBackedTexture tex = new NativeImageBackedTexture(image);
            tex.upload();
            if (RenderSystem.isOnRenderThread()) {
                MinecraftClient.getInstance().getTextureManager().registerTexture(i, tex);
            } else {
                RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(i, tex));
            }
        } catch (Throwable e) {
            Argon.getInstance().logger.error("Failed registering the buffered texture", e);
        }
    }
}