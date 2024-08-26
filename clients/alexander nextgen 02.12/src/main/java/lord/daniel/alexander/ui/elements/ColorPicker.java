package lord.daniel.alexander.ui.elements;

import lombok.Getter;
import lord.daniel.alexander.settings.impl.number.color.ColorValue;
import lord.daniel.alexander.util.math.time.MSTimer;
import lord.daniel.alexander.util.render.RenderUtil;
import lord.daniel.alexander.util.render.font.CFont;
import lord.daniel.alexander.util.render.gl11.GLUtil;
import org.lwjgl.BufferUtils;
import org.lwjglx.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;

import static net.minecraft.client.gui.GuiScreen.drawRect;
import static org.lwjglx.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;

@Getter
public class ColorPicker {
    
    final ColorValue value;
    final MSTimer timeHelper = new MSTimer();

    int currentValue;

    float x, y, width, height;
    int color = Color.red.getRGB();
    boolean typing;

    String hex;
    Color currentColor;

    public ColorPicker(ColorValue value) {
        this.value = value;
        currentValue = value.getValue().getRGB();
        hex = Integer.toHexString(value.getValue().getRGB()).substring(2);
    }

    public ColorPicker(int current, CFont renderFont) {
        this.value = null;
        this.currentValue = current;
        hex = Integer.toHexString(currentValue).substring(2);
    }

    private Color getHoverColor() {
        final ByteBuffer rgb = BufferUtils.createByteBuffer(100);
        GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, rgb);
        return new Color(rgb.get(0) & 0xFF, rgb.get(1) & 0xFF, rgb.get(2) & 0xFF);
    }

    public void draw(float x, float y, float width, float height, int mouseX, int mouseY, Color currentColor, CFont renderFont) {
        draw(x, y, width, height, mouseX, mouseY, currentColor, true, renderFont);
    }

    public void draw(float x, float y, float width, float height, int mouseX, int mouseY, Color currentColor, boolean isFront, CFont renderFont) {
        this.currentColor = currentColor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        final float f = (float) (color >> 16 & 255) / 255.0F;
        final float f1 = (float) (color >> 8 & 255) / 255.0F;
        final float f2 = (float) (color & 255) / 255.0F;

        final double h = 1;
        for (int i = 0; i < height; i++) {
            RenderUtil.drawRect((float) (x + width + 10), (float) (y + (h * i)), (float) (x + width + 20), (float) (y + (h * (i + 1))), Color.HSBtoRGB((float) i / height, 1, 1), false);

            if (isFront && Mouse.isButtonDown(0) && mouseX >= x + width + 10 && mouseX <= x + width + 20 && mouseY >= y + (h * i) && mouseY <= y + (h * (i + 1))) {
                color = Color.HSBtoRGB((float) i / height, 1, 1);
            }
        }

        for (int i = 0; i < height; i++) {
            if (color == Color.HSBtoRGB((float) i / height, 1, 1)) {
                RenderUtil.drawRect((float) (x + width + 10), (float) (y + (h * i) - 0.5f), (float) (x + width + 20), (float) (y + (h * (i + 1)) + 1), -1, false);

            }
        }

        glEnable(GL_BLEND);
        glShadeModel(GL_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        GL11.glBegin(GL_QUADS);
        GLUtil.color(new Color(f, f1, f2));
        glVertex2d(x + width, y);
        GLUtil.color(Color.white);
        glVertex2d(x, y);
        GLUtil.color(Color.BLACK);
        glVertex2d(x, y + height);
        GLUtil.color(Color.BLACK);
        glVertex2d(x + width, y + height);
        GL11.glEnd();
        glEnable(GL_TEXTURE_2D);

        if (isFront && Mouse.isButtonDown(0)) {
            if (isHover(mouseX, mouseY)) {
                final Color hoverColor = getHoverColor();
                if (value != null) {
                    value.setValue(hoverColor);
                }
                currentValue = hoverColor.getRGB();
                hex = Integer.toHexString(hoverColor.getRGB()).substring(2);
            }
        }

        RenderUtil.drawRect(x + width + 25, y, x + width + 85, y + 25, currentColor.getRGB(), false);
        RenderUtil.drawRect(x + width + 25, (y + 27 - renderFont.getFontRenderer().getHeight()), x + width + 85, y + 25, Integer.MIN_VALUE, false);
        renderFont.getFontRenderer().drawString("#" + hex + (typing && !timeHelper.hasReached(250) ? "_" : ""), x + width + 27, y + 21 - renderFont.getFontRenderer().getHeight() / 2, Color.white.getRGB());
        timeHelper.hasReached(500, true);
    }

    public void handleClick(int mouseX, int mouseY, int mouseButton, CFont renderFont) {
        //fr.drawString("#" + Integer.toHexString(currentColor.getRGB()).substring(2), x + width + 27, y + 21 - fr.FONT_HEIGHT / 2, -1);
        if (mouseButton == 0) {
            final float xPos = (x + width + 27 + renderFont.getFontRenderer().getStringWidth("#")), yPos = (y + 21 - renderFont.getFontRenderer().getHeight() / 2);
            if (mouseX >= xPos && mouseX <= xPos + renderFont.getFontRenderer().getStringWidth(hex) && mouseY >= yPos && mouseY <= yPos + renderFont.getFontRenderer().getHeight()) {
                typing = true;
            } else {
                typing = false;
            }
        }
    }

    public void handleInput(char typedChar, int keyCode) {
        try {
            if (typing) {
                int digit = Character.digit(typedChar, 16);
                int limit = -Integer.MIN_VALUE;
                if (hex.length() > 0 && hex.charAt(0) == '-')
                    limit = Integer.MIN_VALUE;
                int multmin = limit / 16;

                switch (keyCode) {
                    case KEY_BACK:
                        if (!hex.isEmpty()) {
                            hex = hex.substring(0, hex.length() - 1);
                            if (!hex.replace("-", "").isEmpty()) {
                                if (value != null)
                                    value.setValue(new Color(Integer.parseInt(hex, 16)));
                                currentValue = Integer.parseInt(hex, 16);
                            }
                        }
                        break;
                    case KEY_END:
                    case KEY_RETURN:
                        typing = false;
                        if (!hex.replace("-", "").isEmpty()) {
                            if (value != null) {
                                value.setValue(new Color(Integer.parseInt(hex, 16)));
                            }
                            currentValue = Integer.parseInt(hex, 16);
                        }
                        break;
                    default:
                        if ((typedChar == '-' || digit >= 0)) {
                            hex = hex + typedChar;
                            if (hex.replace("-", "").length() > 0) {
                                if (value != null)
                                    value.setValue(new Color(Integer.parseInt(hex, 16)));
                                currentValue = Integer.parseInt(hex, 16);
                            }
                        }
                        break;
                }
            }
        } catch (NumberFormatException e) {
            if (hex.length() > 0) {
                hex = hex.substring(0, hex.length() - 1);
            }
        }
    }

    public boolean isHover(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public enum Type {
        QUAD, SHADER;
    }
}