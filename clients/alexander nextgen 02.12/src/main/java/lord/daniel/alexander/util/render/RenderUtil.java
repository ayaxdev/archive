package lord.daniel.alexander.util.render;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.util.math.MathUtil;
import lord.daniel.alexander.util.render.gl11.GLUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@UtilityClass
public class RenderUtil {

    public void drawRect(float left, float top, float right, float bottom, Color color) {
        drawRect(left, top, right, bottom, color.getRGB(), true);
    }

    public void drawRect(float left, float top, float right, float bottom, int color) {
        drawRect(left, top, right, bottom, color, true);
    }

    public void drawRect(float left, float top, float right, float bottom, Color color, boolean fixedRightAndBottom) {
        drawRect(left, top, right, bottom, color.getRGB(), fixedRightAndBottom);
    }

    public void drawRect(float left, float top, float right, float bottom, int color, boolean fixedRightAndBottom) {
        if(fixedRightAndBottom) {
            right += left;
            bottom += top;
        }

        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawClickGuiArrow(float x, float y, float size, float progress, int color) {
        GL11.glTranslatef(x, y, 0);
        GLUtil.color(color);

        GLUtil.setup2DRendering();

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        double interpolation = MathUtil.interpolate(0.0, size / 2.0, progress);
        if (progress >= .48) {
            GL11.glVertex2d(size / 2f, MathUtil.interpolate(size / 2.0, 0.0, progress));
        }
        GL11.glVertex2d(0, interpolation);

        if (progress < .48) {
            GL11.glVertex2d(size / 2f, MathUtil.interpolate(size / 2.0, 0.0, progress));
        }
        GL11.glVertex2d(size, interpolation);

        GL11.glEnd();

        GLUtil.end2DRendering();

        GL11.glTranslatef(-x, -y, 0);

        GlStateManager.resetColor();
    }


    public static void drawCheckMark(float x, float y, int width, int color) {
        float f = (float) (color >> 24 & 255) / 255.0F;
        float f1 = (float) (color >> 16 & 255) / 255.0F;
        float f2 = (float) (color >> 8 & 255) / 255.0F;
        float f3 = (float) (color & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d((double) (x + (float) width) - 6.5D, (y + 4.0F));
        GL11.glVertex2d((double) (x + (float) width) - 11.5D, (y + 10.0F));
        GL11.glVertex2d((double) (x + (float) width) - 13.5D, (y + 8.0F));
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.resetColor();
    }


}
