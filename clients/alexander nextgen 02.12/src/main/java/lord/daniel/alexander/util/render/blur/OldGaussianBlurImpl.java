package lord.daniel.alexander.util.render.blur;

import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.math.MathUtil;
import lord.daniel.alexander.util.render.gl11.GLUtil;
import lord.daniel.alexander.util.render.shader.ShaderProgram;
import lord.daniel.alexander.util.render.shader.util.ShaderUtil;
import lord.daniel.alexander.util.render.shader.util.StencilUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjglx.opengl.Display;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Written by Daniel. on 23/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class OldGaussianBlurImpl implements IMinecraft {

    private final ShaderProgram shaderProgram = new ShaderProgram(ShaderUtil.GAUSSIAN_BLUR);

    private Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public void renderBlur(List<Runnable> runnables, float radius) {
        if (!runnables.isEmpty() && Display.isActive()) {
            StencilUtil.initStencilToWrite();
            runnables.forEach(Runnable::run);
            StencilUtil.readStencilBuffer(1);

            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 1);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            framebuffer = ShaderUtil.updateFramebuffer(framebuffer);
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);

            shaderProgram.use();
            setupUniforms(1, 0, radius);

            GLUtil.bindTexture(mc.getFramebuffer().framebufferTexture);

            ShaderUtil.drawFullScreenQuad();
            framebuffer.unbindFramebuffer();
            shaderProgram.unbind();

            mc.getFramebuffer().bindFramebuffer(true);
            shaderProgram.use();
            setupUniforms(0, 1, radius);

            GLUtil.bindTexture(framebuffer.framebufferTexture);
            ShaderUtil.drawFullScreenQuad();
            shaderProgram.unbind();

            GlStateManager.resetColor();
            GlStateManager.bindTexture(0);

            StencilUtil.unbindStencilBuffer();
        }
    }


    public void setupUniforms(float dir1, float dir2, float radius) {
        shaderProgram.setUniformInt("textureIn", 0);
        shaderProgram.setUniformFloat("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        shaderProgram.setUniformFloat("direction", dir1, dir2);
        shaderProgram.setUniformFloat("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        GL20.glUniform1fv(shaderProgram.getUniformLocation("weights"), weightBuffer);
    }

}
