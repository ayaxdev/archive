package lord.daniel.alexander.util.render.rounded;

import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.render.color.ColorUtil;
import lord.daniel.alexander.util.render.gl11.GLUtil;
import lord.daniel.alexander.util.render.shader.ShaderProgram;
import lord.daniel.alexander.util.render.shader.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

public class RoundedUtil implements IMinecraft {

    private static final ShaderProgram roundedShader = new ShaderProgram(ShaderUtil.ROUNDED_RECT);
    private static final ShaderProgram roundedGradientShader = new ShaderProgram(ShaderUtil.ROUNDED_RECT_GRADIENT);
    private static final ShaderProgram roundedRectOutline = new ShaderProgram(ShaderUtil.ROUNDED_RECT_OUTLINE);

    public static void drawRoundedRectangle(float x, float y, float width, float height, float radius, Color color) {
        drawRoundedRectangle(x, y, width, height, radius, false, color);
    }

    public static void drawHorizontalGradient(float x, float y, float width, float height, float radius, Color leftColor, Color rightColor) {
        drawGradientRoundedRectangle(x, y, width, height, radius, leftColor, leftColor, rightColor, rightColor);
    }

    public static void drawVerticalGradient(float x, float y, float width, float height, float radius, Color topColor, Color bottomColor) {
        drawGradientRoundedRectangle(x, y, width, height, radius, bottomColor, topColor, bottomColor, topColor);
    }

    public static void drawCornerGradientLR(float x, float y, float width, float height, float radius, Color topLeftColor, Color bottomRightColor) {
        Color mixedColor = ColorUtil.interpolateColor(topLeftColor, bottomRightColor, .5f);
        drawGradientRoundedRectangle(x, y, width, height, radius, mixedColor, topLeftColor, bottomRightColor, mixedColor);
    }

    public static void drawCornerGradientRL(float x, float y, float width, float height, float radius, Color bottomLeftColor, Color topRightColor) {
        Color mixedColor = ColorUtil.interpolateColor(topRightColor, bottomLeftColor, .5f);
        drawGradientRoundedRectangle(x, y, width, height, radius, bottomLeftColor, mixedColor, mixedColor, topRightColor);
    }

    public static void drawGradientRoundedRectangle(float x, float y, float width, float height, float radius, Color bottomLeftColor, Color topLeftColor, Color bottomRightColor, Color topRightColor) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0);
        GlStateManager.resetColor();
        GLUtil.startBlend();
        roundedGradientShader.use();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);

        roundedGradientShader.setUniformFloat("color1", getColorComponent(topLeftColor));
        roundedGradientShader.setUniformFloat("color2", getColorComponent(bottomLeftColor));
        roundedGradientShader.setUniformFloat("color3", getColorComponent(topRightColor));
        roundedGradientShader.setUniformFloat("color4", getColorComponent(bottomRightColor));

        ShaderUtil.drawQuad(x - 1, y - 1, width + 2, height + 2);
        roundedGradientShader.unbind();
        GLUtil.endBlend();
    }

    public static void drawRoundedRectangle(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        GlStateManager.resetColor();
        GLUtil.startBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0);
        roundedShader.use();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformInt("blur", blur ? 1 : 0);
        roundedShader.setUniformFloat("color", getColorComponent(color));

        ShaderUtil.drawQuad(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unbind();
        GLUtil.endBlend();
    }

    public static void drawRoundedRectangleOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        GlStateManager.resetColor();
        GLUtil.startBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0);
        roundedRectOutline.use();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedRectOutline);
        roundedRectOutline.setUniformFloat("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedRectOutline.setUniformFloat("color", getColorComponent(color));
        roundedRectOutline.setUniformFloat("outlineColor", getColorComponent(outlineColor));

        ShaderUtil.drawQuad(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedRectOutline.unbind();
        GLUtil.endBlend();
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderProgram roundedShader) {
        ScaledResolution sr = new ScaledResolution(mc);
        roundedShader.setUniformFloat("location", x * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedShader.setUniformFloat("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedShader.setUniformFloat("radius", radius * sr.getScaleFactor());
    }

    private static float[] getColorComponent(Color color) {
        return new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f};
    }
}
