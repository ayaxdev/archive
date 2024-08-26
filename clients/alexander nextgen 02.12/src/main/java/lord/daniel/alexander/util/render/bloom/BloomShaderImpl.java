package lord.daniel.alexander.util.render.bloom;

import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.render.shader.ShaderProgram;
import lord.daniel.alexander.util.render.shader.util.ShaderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjglx.BufferUtils;
import org.lwjglx.opengl.Display;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Written by Daniel. on 23/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class BloomShaderImpl implements IMinecraft {

    private final ShaderProgram shaderProgram = new ShaderProgram(ShaderUtil.BLOOM_SHADER);

    private Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    public void renderBloom(List<Runnable> runnables, float radius) {
        if (!runnables.isEmpty() && Display.isActive()) {
            if (mc.displayWidth != input.framebufferWidth || mc.displayHeight != input.framebufferHeight) {
                input.deleteFramebuffer();
                input = ShaderUtil.updateFramebuffer(input);

                output.deleteFramebuffer();
                output = ShaderUtil.updateFramebuffer(output);
            } else {
                input.framebufferClear();
                output.framebufferClear();
            }
            input.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
            output.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

            this.input.bindFramebuffer(true);

            runnables.forEach(Runnable::run);

            this.output.bindFramebuffer(true);
            this.shaderProgram.use();

            final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);

            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(calculateGaussianValue(i, radius / 2f));
            }

            weightBuffer.rewind();
            setupUniforms(radius, weightBuffer);
            shaderProgram.setUniformFloat("direction", 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            input.bindFramebufferTexture();
            ShaderUtil.drawFullScreenQuad();
            mc.getFramebuffer().bindFramebuffer(true);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            shaderProgram.setUniformFloat("direction", 0.0f, 1.0f);
            output.bindFramebufferTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE16);
            input.bindFramebufferTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            ShaderUtil.drawFullScreenQuad();
            GlStateManager.disableBlend();

            shaderProgram.unbind();
        }
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    private void setupUniforms(float radius, FloatBuffer buffer) {
        shaderProgram.setUniformInt("inTexture", 0);
        shaderProgram.setUniformInt("textureToCheck", 16);
        shaderProgram.setUniformFloat("radius", radius);
        shaderProgram.setUniformFloat("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        OpenGlHelper.glUniform1(shaderProgram.getUniformLocation("weights"), buffer);
    }

}
