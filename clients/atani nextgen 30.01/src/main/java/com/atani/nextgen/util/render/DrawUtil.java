package com.atani.nextgen.util.render;

import com.atani.nextgen.util.render.gl.GLUtil;
import com.atani.nextgen.util.render.gl.VertexUtils;
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
        worldrenderer.pos((double)x, (double)y2, 0.0D).endVertex();
        worldrenderer.pos((double)x2, (double)y2, 0.0D).endVertex();
        worldrenderer.pos((double)x2, (double)y, 0.0D).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0D).endVertex();
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

}