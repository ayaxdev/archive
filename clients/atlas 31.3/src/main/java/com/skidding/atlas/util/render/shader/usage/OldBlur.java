package com.skidding.atlas.util.render.shader.usage;

import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.gl.GLUtil;
import com.skidding.atlas.util.render.shader.Shader;
import com.skidding.atlas.util.render.shader.factory.ShaderFactory;
import com.skidding.atlas.util.render.shader.input.Shaders;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.List;

public class OldBlur implements IMinecraft {

    private final Shader shaderProgram = ShaderFactory.createUsingSource(Shaders.GAUSSIAN.source, Shaders.VERTEX.source);

    private Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public void renderBlur(List<ShaderRenderer.ShaderDrawContext> drawContexts, float range) {
        if (!drawContexts.isEmpty() && Display.isActive()) {
            initStencilToWrite();
            for(ShaderRenderer.ShaderDrawContext shaderDrawContext : drawContexts) {
                shaderDrawContext.draw(true);
            }
            readStencilBuffer(1);

            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 1);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            framebuffer = updateFramebuffer(framebuffer);
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);

            shaderProgram.use();
            setupUniforms(1, 0, range);

            GLUtil.bindTexture(mc.getFramebuffer().framebufferTexture);

            GLUtil.drawQuads();
            framebuffer.unbindFramebuffer();
            shaderProgram.unload();

            mc.getFramebuffer().bindFramebuffer(true);
            shaderProgram.use();
            setupUniforms(0, 1, range);

            GLUtil.bindTexture(framebuffer.framebufferTexture);
            GLUtil.drawQuads();
            shaderProgram.unload();

            GlStateManager.resetColor();
            GlStateManager.bindTexture(0);

            unbindStencilBuffer();
        }
    }


    public void setupUniforms(float dir1, float dir2, float radius) {
        shaderProgram.setUniformi("textureIn", 0);
        shaderProgram.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        shaderProgram.setUniformf("direction", dir1, dir2);
        shaderProgram.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        GL20.glUniform1(shaderProgram.getUniform("weights"), weightBuffer);
    }

    public static void initStencilToWrite() {
        //init
        mc.getFramebuffer().bindFramebuffer(false);
        checkSetupFBO(mc.getFramebuffer());
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);

        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glColorMask(false, false, false, false);
    }

    public static void checkSetupFBO(Framebuffer framebuffer) {
        if (framebuffer != null) {
            if (framebuffer.depthBuffer > -1) {
                setupFBO(framebuffer);
                framebuffer.depthBuffer = -1;
            }
        }
    }


    public static void setupFBO(Framebuffer framebuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
        final int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    }


    public static void readStencilBuffer(int ref) {
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, ref, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public static void unbindStencilBuffer() {
        glDisable(GL_STENCIL_TEST);
    }


    public static Framebuffer updateFramebuffer(final Framebuffer framebuffer, boolean depth) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static Framebuffer updateFramebuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

}
