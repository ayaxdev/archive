package com.skidding.atlas.util.render.gl;

import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@UtilityClass
public class VertexUtils {

    public void preRender(float width) {
        GlStateManager.pushMatrix();
        GLUtil.preRenderShade();
        GL11.glLineWidth(width);
    }

    public void postRenderer() {
        GLUtil.postRenderShade();
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    public void start(int mode) {
        GL11.glBegin(mode);
    }

    public void add(double x, double y, Color color) {
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glVertex2d(x, y);
    }

    public void end() {
        GL11.glEnd();
    }
}