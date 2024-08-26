package com.daniel.datsuzei.util.render;

import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@UtilityClass
public class DrawUtil implements MinecraftClient {

    public void drawRectAbsolute(float left, float top, float right, float bottom, int color) {
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
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void drawRectRelative(float left, float top, float width, float height, int color) {
        drawRectAbsolute(left, top, left + width, top + height, color);
    }

    public void drawBorderAbsolute(float left, float top, float right, float bottom, float borderSize, int color, boolean borderInBounds) {
        float adjustedLeft = left;
        float adjustedTop = top;
        float adjustedRight = right;
        float adjustedBottom = bottom;

        if (!borderInBounds) {
            adjustedLeft -= borderSize;
            adjustedTop -= borderSize;
            adjustedRight += borderSize;
            adjustedBottom += borderSize;
        }

        // Draw the border
        drawRectAbsolute(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderSize, color); // Top
        drawRectAbsolute(adjustedLeft, adjustedBottom - borderSize, adjustedRight, adjustedBottom, color); // Bottom
        drawRectAbsolute(adjustedLeft, adjustedTop + borderSize, adjustedLeft + borderSize, adjustedBottom - borderSize, color); // Left
        drawRectAbsolute(adjustedRight - borderSize, adjustedTop + borderSize, adjustedRight, adjustedBottom - borderSize, color); // Right
    }

    public void drawBorderRelative(float left, float top, float width, float height, float borderSize, int color, boolean borderInBounds) {
        drawBorderAbsolute(left, top, left + width, top + height, borderSize, color, borderInBounds);
    }

}
