package com.daniel.datsuzei.font;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import com.daniel.datsuzei.util.math.MathUtil;
import com.daniel.datsuzei.util.render.font.FontCharacter;
import com.daniel.datsuzei.util.render.gl.GLUtil;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ClientFontRenderer extends FontRenderer implements MinecraftClient, Feature {

    private static final String ALPHABET = "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";
    private static final String COLOR_CODE_CHARACTERS = "0123456789abcdefklmnor";
    private static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    private static final float SCALE = 0.5f;
    private static final float SCALE_INVERSE = 1 / SCALE;
    private static final char COLOR_INVOKER = '\247';
    private static final int[] COLOR_CODES = new int[32];
    private static final int LATIN_START = 0, LATIN_END = 255,
            CJK_START = 11904, CJK_END = 40959;
    private static final int INT_MAX_AMOUNT = 65535;
    private static final int MARGIN_WIDTH = 4;
    private static final int MASK = 0xFF;

    private final Font font;
    private final boolean fractionalMetrics;
    private final float fontHeight;
    private final FontCharacter[] defaultCharacters = new FontCharacter[INT_MAX_AMOUNT];
    private final FontCharacter[] boldCharacters = new FontCharacter[INT_MAX_AMOUNT];
    private boolean antialiasing = true;

    private final boolean allowCJK;

    @Getter
    private final String family, type;

    @Getter
    private final float size;


    public ClientFontRenderer(final String family, final String type, final float size, final Font font) {
        this(family, type, size, font, true);
    }

    public ClientFontRenderer(final String family, final String type, final float size, final Font font, final boolean fractionalMetrics, final boolean antialiasing) {
        super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), mc.isUnicode());
        this.family = family;
        this.type = type;
        this.size = size;
        this.antialiasing = antialiasing;
        this.font = font;
        this.allowCJK = font.canDisplay(DatsuzeiClient.CJ_NAME.charAt(0));
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), antialiasing, fractionalMetrics)).getHeight() / 2);
        this.fillCharacters(this.defaultCharacters, Font.PLAIN);
        this.fillCharacters(this.boldCharacters, Font.BOLD);
        this.FONT_HEIGHT = (int) getHeight();
    }

    public ClientFontRenderer(final String family, final String type, final float size, final Font font, final boolean fractionalMetrics) {
        super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), mc.isUnicode());
        this.family = family;
        this.type = type;
        this.size = size;
        this.font = font;
        this.allowCJK = font.canDisplay(DatsuzeiClient.CJ_NAME.charAt(0));
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), true, fractionalMetrics)).getHeight() / 2);
        this.fillCharacters(this.defaultCharacters, Font.PLAIN);
        this.fillCharacters(this.boldCharacters, Font.BOLD);
        this.FONT_HEIGHT = (int) fontHeight;
    }

    public static void calculateColorCodes() {
        for (int i = 0; i < 32; ++i) {
            final int amplifier = (i >> 3 & 1) * 85;
            int red = (i >> 2 & 1) * 170 + amplifier;
            int green = (i >> 1 & 1) * 170 + amplifier;
            int blue = (i & 1) * 170 + amplifier;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            COLOR_CODES[i] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }
    }

    /**
     * Populate a given {@link FontCharacter} array. The process of instantiating a FontCharacter calculates the
     * height and width of the character, as well as uploading the texture to OpenGL and storing the OpenGL texture
     * ID for rendering later. This process must be run for every character before it is rendered, as is done in this
     * method.
     *
     * @param characters A reference to a FontCharacter array to populate
     * @param style      The font style to use, as defined in {@link Font}. Acceptable values are 0 = PLAIN, 1 = BOLD,
     *                   2 = ITALIC.
     */
    public void fillCharacters(final FontCharacter[] characters, final int style) {
        final Font font = this.font.deriveFont(style);
        final BufferedImage fontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D fontGraphics = (Graphics2D) fontImage.getGraphics();
        final FontMetrics fontMetrics = fontGraphics.getFontMetrics(font);

        int[] chars = MathUtil.range(LATIN_START, LATIN_END);
        if(allowCJK)
            chars = ArrayUtils.addAll(MathUtil.range(CJK_START, CJK_END), chars);

        for (int i : chars) {
            final int fixed = Math.max(0, i - 1);
            final char character = (char) fixed;
            final Rectangle2D charRectangle = fontMetrics.getStringBounds(character + "", fontGraphics);

            // Draw the character. This is cached into an OpenGL texture so that this process doesn't need to
            // be repeated on every frame that the character is later rendered on.
            final BufferedImage charImage = new BufferedImage(MathHelper.ceiling_float_int(
                    (float) charRectangle.getWidth()) + MARGIN_WIDTH * 2, MathHelper.ceiling_float_int(
                    (float) charRectangle.getHeight() + 5), BufferedImage.TYPE_INT_ARGB);

            final Graphics2D charGraphics = (Graphics2D) charImage.getGraphics();
            charGraphics.setFont(font);

            // Calculate the width and height of the character
            final int width = charImage.getWidth();
            final int height = charImage.getHeight();
            charGraphics.setColor(TRANSPARENT_COLOR);
            charGraphics.fillRect(0, 0, width, height);
            setRenderHints(charGraphics);
            charGraphics.drawString(character + "", MARGIN_WIDTH, font.getSize());

            // Generate a new OpenGL texture, and pass it along to uploadTexture() with the image of the character so
            // that it can be stored as a complete OpenGL texture for later use
            final int charTexture = GL11.glGenTextures();
            uploadTexture(charTexture, charImage, width, height);

            // Store the completed character back into the provided character array
            characters[fixed] = new FontCharacter(charTexture, (float) width, (float) height);
        }
    }

    public void setRenderHints(final Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        if (antialiasing) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    public void uploadTexture(final int texture, final BufferedImage image, final int width, final int height) {
        final int[] pixels = image.getRGB(0, 0, width, height, new int[width * height], 0, width);
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * MARGIN_WIDTH);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int pixel = pixels[x + y * width];
                byteBuffer.put((byte) ((pixel >> 16) & MASK));
                byteBuffer.put((byte) ((pixel >> 8) & MASK));
                byteBuffer.put((byte) (pixel & MASK));
                byteBuffer.put((byte) ((pixel >> 24) & MASK));
            }
        }
        byteBuffer.flip();
        GlStateManager.bindTexture(texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
    }

    public int drawString(final String text, final float x, final float y, final int color) {
        return drawString(text, x, y, color, false);
    }

    public int drawCenteredString(final String text, final float x, final float y, final int color) {
        return drawString(text, x - (float)(this.getStringWidth(text) / 2), y, color, false);
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x + fontHeight / 15f, y + fontHeight / 15f, color, true);
        return this.drawString(text, x, y, color);
    }
    public int drawCenteredStringWithShadow(String text, float x, float y, int color) {
        return this.drawStringWithShadow(text, x - (float)(this.getStringWidth(text) / 2), y, color);
    }

    public int drawTotalCenteredString(String string, float x, float y, int color) {
        return this.drawString(string, x - (float)(this.getStringWidth(string) / 2), y - (float)(this.fontHeight / 2), color);
    }

    public int drawTotalCenteredStringWithShadow(String string, float x, float y, int color) {
        return this.drawStringWithShadow(string, x - (float)(this.getStringWidth(string) / 2), y - (float)(this.fontHeight / 2), color);
    }

    @Override
    public int drawString(String text, float x, float y, final int color, final boolean shadow) {
        if(!allowCJK) {
            StringBuilder textBuilder = new StringBuilder();
            for (char c : text.toCharArray()) {
                if(((int) c) < 256)
                    textBuilder.append(c);
            }
            text = textBuilder.toString();
        }

        y += 2;
        calculateColorCodes();
        FontCharacter[] characterSet = defaultCharacters;

        double givenX = x;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glScalef(SCALE, SCALE, SCALE);

        x -= MARGIN_WIDTH / SCALE_INVERSE;
        y -= MARGIN_WIDTH / SCALE_INVERSE;
        x *= SCALE_INVERSE;
        y *= SCALE_INVERSE;
        y -= fontHeight / 5;

        final int shadowColor = -16777216; //(color & 0xFCFCFC) >> 2 | color & 0xFF000000

        final float startX = x;

        final int length = text.length();
        GLUtil.color(shadow ? shadowColor : color);
        char previousCharacter = '.';

        for (int i = 0; i < length; ++i) {
            final char character = text.charAt(i);

            try {
                if (character == '\n') {
                    x = startX;
                    y += getHeight() * 2;
                    continue;
                }

                if (previousCharacter != COLOR_INVOKER) {
                    if (character == COLOR_INVOKER) {
                        final int index = COLOR_CODE_CHARACTERS.indexOf(text.toLowerCase().charAt(i + 1));
                        if (index < 16) {
                            GLUtil.color(shadow ? shadowColor : COLOR_CODES[index]);
                        } else if (index == 17) {
                            characterSet = boldCharacters;
                        }
                    } else if (characterSet.length > character) {
                        final FontCharacter fontCharacter = characterSet[character];
                        fontCharacter.render(x, y);
                        x += fontCharacter.width() - MARGIN_WIDTH * 2;
                    }
                }
            } catch (Exception exception) {
                DatsuzeiClient.getSingleton().getLogger().error(STR."Character \{character} (\{(int) character}) was out of bounds for \{characterSet.length}", exception);
            }
            previousCharacter = character;
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GlStateManager.bindTexture(0);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        return (int) (x - givenX);
    }

    @Override
    public int getStringWidth(String text) {
        FontCharacter[] characterSet = defaultCharacters;
        final int length = text.length();
        char previousCharacter = '.';
        float width = 0;

        for (int i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            if (previousCharacter != COLOR_INVOKER) {
                if (character == COLOR_INVOKER) {
                    final int index = COLOR_CODE_CHARACTERS.indexOf(text.toLowerCase().charAt(i + 1));
                    if (index < 16) {
                        characterSet = defaultCharacters;
                    } else if (index == 17) {
                        characterSet = boldCharacters;
                    }
                } else if (characterSet.length > character) {
                    width += characterSet[character].width() - MARGIN_WIDTH * 2;
                }
            }
            previousCharacter = character;
        }

        return (int) (width / 2);
    }

    public float getHeight() {
        return fontHeight;
    }

    @Override
    public String getName() {
        return STR."\{family}-\{type}-\{size}";
    }

    @Override
    public JsonObject serializeFeature() {
        throw new IllegalStateException("Cannot serialize a font!");
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        throw new IllegalStateException("Cannot deserialize a font!");
    }
}