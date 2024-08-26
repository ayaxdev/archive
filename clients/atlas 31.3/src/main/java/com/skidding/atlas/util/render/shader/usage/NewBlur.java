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

import java.util.List;

public class NewBlur implements IMinecraft {
    private final Shader shaderProgram = ShaderFactory.createUsingSource(Shaders.BLUR.source, Shaders.VERTEX.source);

    private Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    public void renderBlur(List<ShaderRenderer.ShaderDrawContext> runnables, float blurRadius, float blurSigma) {
        if (!runnables.isEmpty() && Display.isActive()) {
            if(RenderUtil.needsFramebufferUpdate(input)) {
                input = RenderUtil.createFrameBuffer(input);
                output = RenderUtil.createFrameBuffer(output);
            } else {
                input.framebufferClear();
                output.framebufferClear();
            }

            input.bindFramebuffer(true);

            runnables.forEach(shaderDrawContext -> shaderDrawContext.draw(true));

            output.bindFramebuffer(true);
            
            shaderProgram.use();

            setupUniforms(blurRadius, blurSigma);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

            mc.getFramebuffer().bindFramebufferTexture();

            GLUtil.drawQuads();

            mc.getFramebuffer().bindFramebuffer(true);
            shaderProgram.setUniformf("coords", 0.0f, 1.0f);
            output.bindFramebufferTexture();

            GL13.glActiveTexture(GL13.GL_TEXTURE20);

            input.bindFramebufferTexture();

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GLUtil.drawQuads();
            GlStateManager.disableBlend();

            shaderProgram.unload();
        }
    }

    private void setupUniforms(float blurRadius, float blurSigma) {
        shaderProgram.setUniformi("currentTexture", 0);
        shaderProgram.setUniformf("blurRadius", blurRadius);
        shaderProgram.setUniformf("blursigma", blurSigma);

        shaderProgram.setUniformf("texelSize", (float) (1.0 / mc.displayWidth), (float) (1.0 / mc.displayHeight));
        shaderProgram.setUniformf("coords", 1, 0);

        shaderProgram.setUniformi("texture20", 20);
    }
}
