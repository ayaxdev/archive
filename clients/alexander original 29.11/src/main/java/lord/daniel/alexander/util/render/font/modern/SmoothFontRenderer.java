package lord.daniel.alexander.util.render.font.modern;

import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.module.impl.options.FontRendererOptionModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.RenderUtil;
import lord.daniel.alexander.util.render.font.CFont;
import lord.daniel.alexander.util.render.font.CFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class SmoothFontRenderer extends CFontRenderer implements Methods {

    private FontRendererOptionModule fontRendererOptionModule;
    private static final String ALPHABET = "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";
    private static final String COLOR_CODE_CHARACTERS = "0123456789abcdefklmnor";
    private static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    private static final float SCALE = 0.5f;
    private static final float SCALE_INVERSE = 1 / SCALE;
    private static final char COLOR_INVOKER = '\247';
    private static final int[] COLOR_CODES = new int[32];
    private static final int LATIN_MAX_AMOUNT = 256;
    private static final int INTERNATIONAL_MAX_AMOUNT = 65535;
    private static final int MARGIN_WIDTH = 4;
    private static final int MASK = 0xFF;

    private final Font font;
    private final boolean fractionalMetrics;
    private final float fontHeight;
    private final FontCharacter[] defaultCharacters = new FontCharacter[LATIN_MAX_AMOUNT];
    private final FontCharacter[] boldCharacters = new FontCharacter[LATIN_MAX_AMOUNT];
    private boolean antialiasing = true;

    public SmoothFontRenderer(final CFont cFont, final Font font) {
        this(cFont, font, true);
    }

    public SmoothFontRenderer(final CFont cFont, final Font font, final boolean fractionalMetrics, final boolean antialiasing) {
        super(cFont);
        this.antialiasing = antialiasing;
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), antialiasing, fractionalMetrics)).getHeight() / 2);
        this.fillCharacters(this.defaultCharacters, Font.PLAIN);
        this.fillCharacters(this.boldCharacters, Font.BOLD);
        this.FONT_HEIGHT = (int) getHeight();
    }

    public SmoothFontRenderer(final CFont cFont, final Font font, final boolean fractionalMetrics) {
        super(cFont);
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), true, fractionalMetrics)).getHeight() / 2);
        this.fillCharacters(this.defaultCharacters, Font.PLAIN);
        this.fillCharacters(this.boldCharacters, Font.BOLD);
        this.FONT_HEIGHT = (int) getHeight();
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

        for (int i = 0; i < characters.length; ++i) {
            final char character = (char) i;
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
            characters[i] = new FontCharacter(charTexture, width, height);
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
        return drawString(text, x - (getStringWidth(text) >> 1), y, color, false);
    }

    public int drawRightString(String text, float x, float y, int color) {
        return drawString(text, x - (getStringWidth(text)), y, color, false);
    }

    @Override
    public int drawString(String text, float x, float y, final int color, final boolean shadow) {
        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

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

        final int shadowColor = fontRendererOptionModule.coloredShadow.getValue() ? (color & 0xFCFCFC) >> 2 | color & 0xFF000000 : fontRendererOptionModule.shadowColour.getValue().getRGB();

        final float startX = x;

        final int length = text.length();
        RenderUtil.color(shadow ? shadowColor : color);
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
                            RenderUtil.color(shadow ? shadowColor : COLOR_CODES[index]);
                        } else if (index == -1) {
                            RenderUtil.color(shadow ? shadowColor : color);
                            characterSet = defaultCharacters;
                        } else if (index == 17) {
                            characterSet = boldCharacters;
                        }
                    } else if (characterSet.length > character) {
                        final FontCharacter fontCharacter = characterSet[character];
                        fontCharacter.render((float) x, (float) y);
                        x += fontCharacter.getWidth() - MARGIN_WIDTH * 2;
                    }
                }
            } catch (Exception exception) {
                System.out.println("Character \"" + character + "\" was out of bounds " +
                        "(" + ((int) character) + " out of bounds for " + characterSet.length + ")");
                exception.printStackTrace();
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
                    if (index < 16 || index == -1) {
                        characterSet = defaultCharacters;
                    } else if (index == 17) {
                        characterSet = boldCharacters;
                    }
                } else if (characterSet.length > character) {
                    width += characterSet[character].getWidth() - MARGIN_WIDTH * 2;
                }
            }
            previousCharacter = character;
        }

        return (int) (width / 2);
    }

    public float getHeight() {
        return fontHeight;
    }

}