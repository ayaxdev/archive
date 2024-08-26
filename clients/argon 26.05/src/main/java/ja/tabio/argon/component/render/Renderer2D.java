package ja.tabio.argon.component.render;

import com.mojang.blaze3d.systems.RenderSystem;
import ja.tabio.argon.component.font.FontRenderer;
import ja.tabio.argon.mixin.DrawContextAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.regex.Pattern;

public record Renderer2D(DrawContext drawContext) {

    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9a-f]");

    public void drawRectAbsolute(float x1, float y1, float x2, float y2, int color) {
        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float f = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float g = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float h = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float j = (float) ColorHelper.Argb.getBlue(color) / 255.0F;
        VertexConsumer vertexConsumer = drawContext.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, x1, y1, (float) 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, (float) 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, (float) 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, (float) 0).color(g, h, j, f).next();
        ((DrawContextAccessor) drawContext).tryDrawI();
    }

    public void drawRect(float x, float y, float width, float height, int color) {
        drawRectAbsolute(x, y, x + width, y + height, color);
    }

    public void drawBorder(final float left, final float top, final float right, final float bottom, final float borderWidth, final int borderColor, final boolean borderIncludedInBounds) {
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

        drawRectAbsolute(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderWidth, borderColor); // Top
        drawRectAbsolute(adjustedLeft, adjustedBottom - borderWidth, adjustedRight, adjustedBottom, borderColor); // Bottom
        drawRectAbsolute(adjustedLeft, adjustedTop + borderWidth, adjustedLeft + borderWidth, adjustedBottom - borderWidth, borderColor); // Left
        drawRectAbsolute(adjustedRight - borderWidth, adjustedTop + borderWidth, adjustedRight, adjustedBottom - borderWidth, borderColor); // Right
    }

    public void drawBorderedRect(final float left, final float top, final float right, final float bottom, final float borderWidth, final int insideColor, final int borderColor, final boolean borderIncludedInBounds) {
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

        drawRectAbsolute(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderWidth, borderColor); // Top
        drawRectAbsolute(adjustedLeft, adjustedBottom - borderWidth, adjustedRight, adjustedBottom, borderColor); // Bottom
        drawRectAbsolute(adjustedLeft, adjustedTop + borderWidth, adjustedLeft + borderWidth, adjustedBottom - borderWidth, borderColor); // Left
        drawRectAbsolute(adjustedRight - borderWidth, adjustedTop + borderWidth, adjustedRight, adjustedBottom - borderWidth, borderColor); // Right

        // Draw the main rectangle
        drawRectAbsolute(adjustedLeft + borderWidth, adjustedTop + borderWidth, adjustedRight - borderWidth, adjustedBottom - borderWidth, insideColor);
    }

    public int drawStringOutlined(FontRenderer fontRenderer, String text, float x, float y, int xOffset, int yOffset, int color, int borderColor) {
        y = Math.round(y);
        final String noColors = COLOR_PATTERN.matcher(text).replaceAll("\u00A7r");
        int yes = 0;
        for (int xOff = -xOffset; xOff < xOffset + 1; xOff++) {
            for (int yOff = -yOffset; yOff < yOffset + 1; yOff++) {
                if (xOff * xOff != yOff * yOff) {
                    yes = Math.max(fontRenderer.drawString(this, noColors, xOff / 2f + x, yOff / 2f + y, Color.black.getRGB()), yes);
                }
            }
        }

        fontRenderer.drawString(this, text, x, y, color);

        return yes;
    }

    public void drawGradientLR(float x1, float y1, float x2, float y2, Color startColor, Color endColor) {
        Matrix4f matrix = drawContext.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(startColor.getRGB()).next();
        bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(startColor.getRGB()).next();
        bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(endColor.getRGB()).next();
        bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(endColor.getRGB()).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }

    public void drawGradientTB(float left, float top, float right, float bottom, Color startColor, Color endColor) {
        Matrix4f matrix = drawContext.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, left, top, 0.0F).color(startColor.getRGB()).next();
        bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(endColor.getRGB()).next();
        bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(endColor.getRGB()).next();
        bufferBuilder.vertex(matrix, right, top, 0.0F).color(startColor.getRGB()).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }

    public void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public void endRender() {
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

}
