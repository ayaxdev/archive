package lord.daniel.alexander.util.render.blur;

import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.render.shader.ShaderProgram;
import lord.daniel.alexander.util.render.shader.util.ShaderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjglx.opengl.Display;

import java.util.List;

/**
 * Written by Daniel. on 23/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class NewGaussianBlurImpl implements IMinecraft {

    private final ShaderProgram shaderProgram = new ShaderProgram(ShaderUtil.MODERN_BLUR);

    private Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    public void renderBlur(List<Runnable> runnables, float blurRadius, float blurSigma) {
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

            this.input.bindFramebuffer(true);

            runnables.forEach(Runnable::run);

            this.output.bindFramebuffer(true);
            this.shaderProgram.use();

            setupUniforms(blurRadius, blurSigma);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

            mc.getFramebuffer().bindFramebufferTexture();

            ShaderUtil.drawFullScreenQuad();

            mc.getFramebuffer().bindFramebuffer(true);
            shaderProgram.setUniformFloat("coords", 0.0f, 1.0f);
            output.bindFramebufferTexture();

            GL13.glActiveTexture(GL13.GL_TEXTURE20);

            input.bindFramebufferTexture();

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            ShaderUtil.drawFullScreenQuad();
            GlStateManager.disableBlend();

            shaderProgram.unbind();
        }
    }

    private void setupUniforms(float blurRadius, float blurSigma) {
        shaderProgram.setUniformInt("currentTexture", 0);
        shaderProgram.setUniformFloat("blurRadius", blurRadius);
        shaderProgram.setUniformFloat("blursigma", blurSigma);

        shaderProgram.setUniformFloat("texelSize", (float) (1.0 / mc.displayWidth), (float) (1.0 / mc.displayHeight));
        shaderProgram.setUniformFloat("coords", 1, 0);

        shaderProgram.setUniformInt("texture20", 20);
    }

}
