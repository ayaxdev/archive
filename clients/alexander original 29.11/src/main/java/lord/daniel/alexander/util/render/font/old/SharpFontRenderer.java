package lord.daniel.alexander.util.render.font.old;

import lombok.Getter;
import lord.daniel.alexander.module.impl.options.FontRendererOptionModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.color.ColorUtil;
import lord.daniel.alexander.util.render.font.CFont;
import lord.daniel.alexander.util.render.font.CFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Locale;

public class SharpFontRenderer extends CFontRenderer
{
    private FontRendererOptionModule fontRendererOptionModule;
    @Getter
    private final Font font;
    private final boolean fractionalMetrics;
    private final CharacterData[] regularData;
    private final CharacterData[] boldData;
    private final CharacterData[] italicsData;
    private final int[] colorCodes;
    private static int RANDOM_OFFSET;
    
    public SharpFontRenderer(final CFont cFont, final Font font) {
        this(cFont, font, 256);
    }
    
    public SharpFontRenderer(final CFont cFont, final Font font, final int characterCount) {
        this(cFont, font, characterCount, true);
    }
    
    public SharpFontRenderer(final CFont cFont, final Font font, final boolean fractionalMetrics) {
        this(cFont, font, 256, fractionalMetrics);
    }
    
    public SharpFontRenderer(final CFont cFont, final Font font, final int characterCount, final boolean fractionalMetrics) {
        super(cFont);
        this.colorCodes = new int[32];
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.regularData = this.setup(new CharacterData[characterCount], 0);
        this.boldData = this.setup(new CharacterData[characterCount], 1);
        this.italicsData = this.setup(new CharacterData[characterCount], 2);
    }
    
    private CharacterData[] setup(final CharacterData[] characterData, final int type) {
        this.generateColors();
        final Font font = this.font.deriveFont(type);
        final BufferedImage utilityImage = new BufferedImage(1, 1, 2);
        final Graphics2D utilityGraphics = (Graphics2D)utilityImage.getGraphics();
        utilityGraphics.setFont(font);
        final FontMetrics fontMetrics = utilityGraphics.getFontMetrics();
        for (int index = 0; index < characterData.length; ++index) {
            final char character = (char)index;
            final Rectangle2D characterBounds = fontMetrics.getStringBounds(character + "", utilityGraphics);
            final float width = (float)characterBounds.getWidth() + 8.0f;
            final float height = (float)characterBounds.getHeight();
            final BufferedImage characterImage = new BufferedImage(MathHelper.ceiling_double_int(width), MathHelper.ceiling_double_int(height), 2);
            final Graphics2D graphics = (Graphics2D)characterImage.getGraphics();
            graphics.setFont(font);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());
            graphics.setColor(Color.WHITE);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            graphics.drawString(character + "", 4, fontMetrics.getAscent());
            final int textureId = GlStateManager.generateTexture();
            this.createTexture(textureId, characterImage);
            characterData[index] = new CharacterData(character, (float) characterImage.getWidth(), (float) characterImage.getHeight(), textureId);
        }
        return characterData;
    }
    
    private void createTexture(final int textureId, final BufferedImage image) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                final int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte)(pixel >> 16 & 0xFF));
                buffer.put((byte)(pixel >> 8 & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
                buffer.put((byte)(pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        GlStateManager.bindTexture(textureId);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 6408, image.getWidth(), image.getHeight(), 0, 6408, 5121, buffer);
        GlStateManager.bindTexture(0);
    }

    @Override
    public int drawString(final String text, float x, float y, int color, final boolean shadow) {
        if (text.isEmpty()) {
            return 0;
        }

        if(fontRendererOptionModule == null)
            fontRendererOptionModule = ModuleStorage.getModuleStorage().getByClass(FontRendererOptionModule.class);

        if(shadow)
            color = fontRendererOptionModule.coloredShadow.getValue() ? (color & 0xFCFCFC) >> 2 | color & 0xFF000000 : fontRendererOptionModule.shadowColour.getValue().getRGB();
        float startX = x;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 1.0);
        x -= 2f;
        y += 1f;
        x *= 2.0f;
        y *= 2.0f;
        CharacterData[] characterData = this.regularData;
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;
        final int length = text.length();
        final float multiplier = (float)(1);
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        GlStateManager.color(r / multiplier, g / multiplier, b / multiplier, a);
        for (int i = 0; i < length; ++i) {
            char character = text.charAt(i);
            final char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != ColorUtil.COLOR_INVOKER) {
                if (character == ColorUtil.COLOR_INVOKER) {
                    int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index < 16) {
                        obfuscated = false;
                        strikethrough = false;
                        underlined = false;
                        characterData = this.regularData;
                        if (index < 0) {
                            index = 15;
                        }
                        if (shadow) {
                            index += 16;
                        }
                        final int textColor = this.colorCodes[index];
                        GL11.glColor4d((textColor >> 16) / 255.0, (textColor >> 8 & 0xFF) / 255.0, (textColor & 0xFF) / 255.0, a);
                    }
                    else if (index == 16) {
                        obfuscated = true;
                    }
                    else if (index == 17) {
                        characterData = this.boldData;
                    }
                    else if (index == 18) {
                        strikethrough = true;
                    }
                    else if (index == 19) {
                        underlined = true;
                    }
                    else if (index == 20) {
                        characterData = this.italicsData;
                    }
                    else {
                        obfuscated = false;
                        strikethrough = false;
                        underlined = false;
                        characterData = this.regularData;
                        GL11.glColor4d(1, 1, 1, a);
                    }
                }
                else if (character <= 'ÿ') {
                    if (obfuscated) {
                        character += (char) SharpFontRenderer.RANDOM_OFFSET;
                    }
                    this.drawChar(character, characterData, x, y);
                    final CharacterData charData = characterData[character];
                    if (strikethrough) {
                        this.drawLine(new Vector2f(0.0f, charData.height / 2.0f), new Vector2f(charData.width, charData.height / 2.0f));
                    }
                    if (underlined) {
                        this.drawLine(new Vector2f(0.0f, charData.height - 15.0f), new Vector2f(charData.width, charData.height - 15.0f));
                    }
                    x += charData.width - 8.0f;
                }
            }
        }
        GL11.glPopMatrix();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        GlStateManager.bindTexture(0);
        GlStateManager.resetColor();
        return (int) (startX - x);
    }
    
    @Override
    public int getStringWidth(final String text) {
        float width = 0.0f;
        CharacterData[] characterData = this.regularData;
        for (int length = text.length(), i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            final char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != ColorUtil.COLOR_INVOKER) {
                if (character == ColorUtil.COLOR_INVOKER) {
                    final int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index == 17) {
                        characterData = this.boldData;
                    }
                    else if (index == 20) {
                        characterData = this.italicsData;
                    }
                    else if (index == 21) {
                        characterData = this.regularData;
                    }
                }
                else if (character <= 'ÿ') {
                    final CharacterData charData = characterData[character];
                    width += (charData.width - 8.0f) / 2.0f;
                }
            }
        }
        return (int) (width + 2.0f);
    }
    
    @Override
    public float getHeight() {
        return this.getHeight("ABCDERGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwkyz1234567890");
    }
    
    public float getHeight(final String text) {
        float height = 0.0f;
        CharacterData[] characterData = this.regularData;
        for (int length = text.length(), i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            final char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != ColorUtil.COLOR_INVOKER) {
                if (character == ColorUtil.COLOR_INVOKER) {
                    final int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index == 17) {
                        characterData = this.boldData;
                    }
                    else if (index == 20) {
                        characterData = this.italicsData;
                    }
                    else if (index == 21) {
                        characterData = this.regularData;
                    }
                }
                else if (character <= 'ÿ') {
                    final CharacterData charData = characterData[character];
                    height = Math.max(height, charData.height);
                }
            }
        }
        return height / 2.0f - 2.0f;
    }
    
    private void drawChar(final char character, final CharacterData[] characterData, final float x, final float y) {
        final CharacterData charData = characterData[character];
        charData.bind();
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(x, y + charData.height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(x + charData.width, y + charData.height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(x + charData.width, y);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glBindTexture(3553, 0);
    }
    
    private void drawLine(final Vector2f start, final Vector2f end) {
        GL11.glDisable(3553);
        GL11.glLineWidth((float) 3.0);
        GL11.glBegin(1);
        GL11.glVertex2f(start.x, start.y);
        GL11.glVertex2f(end.x, end.y);
        GL11.glEnd();
        GL11.glEnable(3553);
    }
    
    private void generateColors() {
        for (int i = 0; i < 32; ++i) {
            final int thingy = (i >> 3 & 0x1) * 85;
            int red = (i >> 2 & 0x1) * 170 + thingy;
            int green = (i >> 1 & 0x1) * 170 + thingy;
            int blue = (i & 0x1) * 170 + thingy;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }

    static {
        SharpFontRenderer.RANDOM_OFFSET = 1;
    }
    
    static class CharacterData
    {
        public char character;
        public float width;
        public float height;
        private final int textureId;
        
        public CharacterData(final char character, final float width, final float height, final int textureId) {
            this.character = character;
            this.width = width;
            this.height = height;
            this.textureId = textureId;
        }
        
        public void bind() {
            GL11.glBindTexture(3553, this.textureId);
        }
    }
}
