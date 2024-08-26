package com.skidding.atlas.util.render.shader.usage.util;

import com.skidding.atlas.util.render.color.ColorUtil;
import com.skidding.atlas.util.render.gl.GLUtil;
import com.skidding.atlas.util.render.shader.Shader;
import com.skidding.atlas.util.render.shader.factory.ShaderFactory;
import com.skidding.atlas.util.render.shader.input.Shaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class DrawRoundUtil {

    public static final DrawRoundUtil INSTANCE = new DrawRoundUtil();

    private final Shader roundedShader = ShaderFactory.createUsingSource(Shaders.ROUNDED_RECT.source, Shaders.VERTEX.source);
    private final Shader roundedOutlineShader = ShaderFactory.createUsingSource(Shaders.OUTLINED_ROUNDED_RECT.source, Shaders.VERTEX.source);
    private final Shader roundedTexturedShader = ShaderFactory.createUsingSource(Shaders.TEXTURED_ROUNDED_RECT.source, Shaders.VERTEX.source);
    private final Shader roundedGradientShader = ShaderFactory.createUsingSource(Shaders.GRADIENT_ROUNDED_RECT.source, Shaders.VERTEX.source);

    public void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }

    public void drawGradientHorizontal(float x, float y, float width, float height, float radius, Color left, Color right) {
        drawGradientRound(x, y, width, height, radius, left, left, right, right);
    }
    public void drawGradientVertical(float x, float y, float width, float height, float radius, Color top, Color bottom) {
        drawGradientRound(x, y, width, height, radius, bottom, top, bottom, top);
    }
    public void drawGradientCornerLR(float x, float y, float width, float height, float radius, Color topLeft, Color bottomRight) {
        Color mixedColor = ColorUtil.interpolateColorC(topLeft, bottomRight, .5f);
        drawGradientRound(x, y, width, height, radius, mixedColor, topLeft, bottomRight, mixedColor);
    }

    public void drawGradientCornerRL(float x, float y, float width, float height, float radius, Color bottomLeft, Color topRight) {
        Color mixedColor = ColorUtil.interpolateColorC(topRight, bottomLeft, .5f);
        drawGradientRound(x, y, width, height, radius, bottomLeft, mixedColor, mixedColor, topRight);
    }

    public void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        GLUtil.setAlphaLimit(0);
        GLUtil.resetColor();
        GLUtil.startBlend();
        roundedGradientShader.use();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        //Top left
        roundedGradientShader.setUniformf("color1", topLeft.getRed() / 255f, topLeft.getGreen() / 255f, topLeft.getBlue() / 255f, topLeft.getAlpha() / 255f);
        // Bottom Left
        roundedGradientShader.setUniformf("color2", bottomLeft.getRed() / 255f, bottomLeft.getGreen() / 255f, bottomLeft.getBlue() / 255f, bottomLeft.getAlpha() / 255f);
        //Top Right
        roundedGradientShader.setUniformf("color3", topRight.getRed() / 255f, topRight.getGreen() / 255f, topRight.getBlue() / 255f, topRight.getAlpha() / 255f);
        //Bottom Right
        roundedGradientShader.setUniformf("color4", bottomRight.getRed() / 255f, bottomRight.getGreen() / 255f, bottomRight.getBlue() / 255f, bottomRight.getAlpha() / 255f);
        GLUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedGradientShader.unload();
        GLUtil.endBlend();
    }




    public void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        GLUtil.resetColor();
        GLUtil.startBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GLUtil.setAlphaLimit(0);
        roundedShader.use();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        GLUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GLUtil.endBlend();
    }


    public void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        GLUtil.resetColor();
        GLUtil.startBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GLUtil.setAlphaLimit(0);
        roundedOutlineShader.use();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);


        GLUtil.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GLUtil.endBlend();
    }


    public void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha) {
        GLUtil.resetColor();
        GLUtil.setAlphaLimit(0);
        GLUtil.startBlend();
        roundedTexturedShader.use();
        roundedTexturedShader.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedTexturedShader);
        roundedTexturedShader.setUniformf("alpha", alpha);
        GLUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedTexturedShader.unload();
        GLUtil.endBlend();
    }

    private void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, Shader roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }


}
