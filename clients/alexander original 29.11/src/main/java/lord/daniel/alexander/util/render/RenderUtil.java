package lord.daniel.alexander.util.render;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class RenderUtil implements Methods {

    public void drawRect(float x, float y, float width, float height, int colour) {
        Gui.drawRect(x, y, x + width, y + height, colour);
    }

    public void drawRect(float x, float y, float width, float height, Color colour) {
        Gui.drawRect(x, y, x + width, y + height, colour.getRGB());
    }

    public static void drawBorder(final float left, final float top, final float right, final float bottom, final float borderWidth, final int borderColor, final boolean borderIncludedInBounds) {
        float adjustedLeft = left;
        float adjustedTop = top;
        float adjustedRight = right;
        float adjustedBottom = bottom;

        if (!borderIncludedInBounds) {
            adjustedLeft -= borderWidth;
            adjustedTop -= borderWidth;
            adjustedRight += borderWidth;
            adjustedBottom += borderWidth;
        }

        // Draw the border
        Gui.drawRect(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderWidth, borderColor); // Top
        Gui.drawRect(adjustedLeft, adjustedBottom - borderWidth, adjustedRight, adjustedBottom, borderColor); // Bottom
        Gui.drawRect(adjustedLeft, adjustedTop + borderWidth, adjustedLeft + borderWidth, adjustedBottom - borderWidth, borderColor); // Left
        Gui.drawRect(adjustedRight - borderWidth, adjustedTop + borderWidth, adjustedRight, adjustedBottom - borderWidth, borderColor); // Right
    }

    public static void drawBorderedRect(final float left, final float top, final float right, final float bottom, final float borderWidth, final int insideColor, final int borderColor, final boolean borderIncludedInBounds) {
        float adjustedLeft = left;
        float adjustedTop = top;
        float adjustedRight = right;
        float adjustedBottom = bottom;

        if (!borderIncludedInBounds) {
            adjustedLeft -= borderWidth;
            adjustedTop -= borderWidth;
            adjustedRight += borderWidth;
            adjustedBottom += borderWidth;
        }

        // Draw the border
        Gui.drawRect(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderWidth, borderColor); // Top
        Gui.drawRect(adjustedLeft, adjustedBottom - borderWidth, adjustedRight, adjustedBottom, borderColor); // Bottom
        Gui.drawRect(adjustedLeft, adjustedTop + borderWidth, adjustedLeft + borderWidth, adjustedBottom - borderWidth, borderColor); // Left
        Gui.drawRect(adjustedRight - borderWidth, adjustedTop + borderWidth, adjustedRight, adjustedBottom - borderWidth, borderColor); // Right

        // Draw the main rectangle
        Gui.drawRect(adjustedLeft + borderWidth, adjustedTop + borderWidth, adjustedRight - borderWidth, adjustedBottom - borderWidth, insideColor);
    }

    public void startScissorBox() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public void drawScissorBox(double x, double y, double width, double height) {
        width = Math.max(width, 0.1);

        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public void drawScissorBox(double x, double y, double width, double height, double scale) {
        width = Math.max(width, 0.1);

        ScaledResolution sr = new ScaledResolution(mc);

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public void endScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void endBlend() {
        GlStateManager.disableBlend();
    }


    public void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float) (limit * .01));
    }

    public void bindTexture(int texture) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }

    public void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(int color) {
        color(new Color(color));
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha / 255f);
    }

    public void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    public void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }


    public boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }


    public static void drawBox(double x, double y, double z, double width, double height) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBegin(GL11.GL_QUADS);

        /* Front */
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x, y, z);

        /* Left */
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x + width, y, z);

        /* Behind */
        GL11.glVertex3d(x, y + height, z + width);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x, y, z + width);

        /* Right */
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x, y + height, z + width);
        GL11.glVertex3d(x, y, z + width);
        GL11.glVertex3d(x, y, z);

        /* Up */
        GL11.glVertex3d(x, y + height, z);
        GL11.glVertex3d(x + width, y + height, z);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x, y + height, z + width);

        /* Down */
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x + width, y, z);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x, y, z + width);

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
