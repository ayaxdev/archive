package com.skidding.atlas.util.render.shader.usage;

import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.RenderUtil;
import com.skidding.atlas.util.render.gl.GLUtil;
import com.skidding.atlas.util.render.shader.Shader;
import com.skidding.atlas.util.render.shader.factory.ShaderFactory;
import com.skidding.atlas.util.render.shader.input.Shaders;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;

public class Bloom implements IMinecraft {

    private int currentIterations;

    public Shader kawaseDown = ShaderFactory.createUsingSource(Shaders.KAWASE_DOWN_BLOOM.source, Shaders.VERTEX.source);
    public Shader kawaseUp = ShaderFactory.createUsingSource(Shaders.KAWASE_UP_BLOOM.source, Shaders.VERTEX.source);

    public Framebuffer framebuffer = new Framebuffer(1, 1, true);

    private final List<Framebuffer> framebufferList = new ArrayList<>();
    private Framebuffer input;

    private void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();

        //Have to make the framebuffer null so that it does not try to delete a framebuffer that has already been deleted
        framebufferList.add(framebuffer = RenderUtil.createFrameBuffer(null, true));

        for (int i = 1; i <= iterations; i++) {
            Framebuffer currentBuffer = new Framebuffer((int) (mc.displayWidth / Math.pow(2, i)), (int) (mc.displayHeight / Math.pow(2, i)), true);
            currentBuffer.setFramebufferFilter(GL_LINEAR);

            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
            GlStateManager.bindTexture(0);

            framebufferList.add(currentBuffer);
        }
    }


    public void renderBloom(List<ShaderRenderer.ShaderDrawContext> drawContexts, int iterations, float offset) {
        if(!drawContexts.isEmpty() && Display.isActive()) {
            input = RenderUtil.createFrameBuffer(input);

            input.framebufferClear();
            input.bindFramebuffer(false);

            for(ShaderRenderer.ShaderDrawContext drawContext : drawContexts)
                drawContext.draw(true);

            input.unbindFramebuffer();

            if (currentIterations != iterations || (framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight)) {
                initFramebuffers(iterations);
                currentIterations = iterations;
            }

            GLUtil.setAlphaLimit(0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_ONE, GL_ONE);

            GL11.glClearColor(0, 0, 0, 0);
            renderFBO(framebufferList.get(1), input.framebufferTexture, kawaseDown, offset);

            //Downsample
            for (int i = 1; i < iterations; i++) {
                renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset);
            }

            //Upsample
            for (int i = iterations; i > 1; i--) {
                renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset);
            }

            Framebuffer lastBuffer = framebufferList.getFirst();
            lastBuffer.framebufferClear();
            lastBuffer.bindFramebuffer(false);
            kawaseUp.use();
            kawaseUp.setUniformf("offset", offset, offset);
            kawaseUp.setUniformi("inTexture", 0);
            kawaseUp.setUniformi("check", 1);
            kawaseUp.setUniformi("textureToCheck", 16);
            kawaseUp.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
            kawaseUp.setUniformf("iResolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
            GLUtil.bindTexture(input.framebufferTexture);
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            GLUtil.bindTexture(framebufferList.get(1).framebufferTexture);
            GLUtil.drawQuads();
            kawaseUp.unload();

            GlStateManager.clearColor(0, 0, 0, 0);
            mc.getFramebuffer().bindFramebuffer(false);
            GLUtil.bindTexture(framebufferList.getFirst().framebufferTexture);
            GLUtil.setAlphaLimit(0);
            GLUtil.startBlend();
            GLUtil.drawQuads();
            GlStateManager.bindTexture(0);
            GLUtil.setAlphaLimit(0);
            GLUtil.startBlend();
        }
    }

    private void renderFBO(Framebuffer framebuffer, int framebufferTexture, Shader shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shader.use();
        GLUtil.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("halfpixel", 1.0f / framebuffer.framebufferWidth, 1.0f / framebuffer.framebufferHeight);
        shader.setUniformf("iResolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);
        GLUtil.drawQuads();
        shader.unload();
    }

}
