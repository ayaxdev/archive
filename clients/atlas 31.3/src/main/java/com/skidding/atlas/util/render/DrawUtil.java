package com.skidding.atlas.util.render;

import com.skidding.atlas.util.render.gl.VertexUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@UtilityClass
public class DrawUtil {

    public void drawRectAbsolute(float x, float y, float x2, float y2, int color) {
        if (x < x2)
        {
            float i = x;
            x = x2;
            x2 = i;
        }

        if (y < y2)
        {
            float j = y;
            y = y2;
            y2 = j;
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
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y, 0.0D).endVertex();
        worldrenderer.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void drawRectRelative(float x, float y, float width, float height, int color) {
        drawRectAbsolute(x, y, x + width, y + height, color);
    }

    public void drawCheck(double x, double y, float checkWidth, float lineWidth, Color color) {
        VertexUtils.preRender(lineWidth);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        VertexUtils.start(GL11.GL_LINE_STRIP);

        VertexUtils.add(x + checkWidth - 6.5D, y + 3.0F, color);
        VertexUtils.add(x + checkWidth - 11.5D, y + 10.0F, color);
        VertexUtils.add(x + checkWidth - 13.5D, y + 8.0F, color);

        VertexUtils.end();
        VertexUtils.postRenderer();
    }

    public static void drawBorderAbsolute(float left, float top, float right, float bottom, float borderSize, int borderColor, boolean borderIncludedInBounds) {
        float adjustedLeft = left;
        float adjustedTop = top;
        float adjustedRight = right;
        float adjustedBottom = bottom;

        if (!borderIncludedInBounds) {
            adjustedLeft -= borderSize;
            adjustedTop -= borderSize;
            adjustedRight += borderSize;
            adjustedBottom += borderSize;
        }

        // Draw the border
        drawRectAbsolute(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderSize, borderColor); // Top
        drawRectAbsolute(adjustedLeft, adjustedBottom - borderSize, adjustedRight, adjustedBottom, borderColor); // Bottom
        drawRectAbsolute(adjustedLeft, adjustedTop + borderSize, adjustedLeft + borderSize, adjustedBottom - borderSize, borderColor); // Left
        drawRectAbsolute(adjustedRight - borderSize, adjustedTop + borderSize, adjustedRight, adjustedBottom - borderSize, borderColor); // Right
    }

    public static void drawBorderedRectAbsolute(float left, float top, float right, float bottom, float borderSize, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        float adjustedLeft = left;
        float adjustedTop = top;
        float adjustedRight = right;
        float adjustedBottom = bottom;

        if (!borderIncludedInBounds) {
            adjustedLeft -= borderSize;
            adjustedTop -= borderSize;
            adjustedRight += borderSize;
            adjustedBottom += borderSize;
        }

        // Draw the border
        drawRectAbsolute(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderSize, borderColor); // Top
        drawRectAbsolute(adjustedLeft, adjustedBottom - borderSize, adjustedRight, adjustedBottom, borderColor); // Bottom
        drawRectAbsolute(adjustedLeft, adjustedTop + borderSize, adjustedLeft + borderSize, adjustedBottom - borderSize, borderColor); // Left
        drawRectAbsolute(adjustedRight - borderSize, adjustedTop + borderSize, adjustedRight, adjustedBottom - borderSize, borderColor); // Right

        // Draw the main rectangle
        drawRectAbsolute(adjustedLeft + borderSize, adjustedTop + borderSize, adjustedRight - borderSize, adjustedBottom - borderSize, insideColor);
    }

    public void drawBorderRelative(float x, float y, float width, float height, float borderSize, int color, boolean borderInBounds) {
        drawBorderAbsolute(x, y, x + width, y + height, borderSize, color, borderInBounds);
    }

    public void drawBorderedRectRelative(float x, float y, float width, float height, float borderSize, int insideColor, int borderColor, boolean borderInBounds) {
        drawBorderedRectAbsolute(x, y, x + width, y + height, borderSize, insideColor, borderColor, borderInBounds);
    }


}
