package lord.daniel.alexander.util.render.shader.shaders;

import java.nio.FloatBuffer;
import java.util.List;

import lord.daniel.alexander.module.impl.hud.ShadowModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.shader.ShaderProgram;
import lord.daniel.alexander.util.render.shader.annotation.Info;
import lord.daniel.alexander.util.render.shader.container.ShaderContainer;
import lord.daniel.alexander.util.render.shader.data.ShaderRenderType;
import lord.daniel.alexander.util.render.shader.data.ShaderType;
import lord.daniel.alexander.util.render.shader.render.FramebufferQuads;
import lord.daniel.alexander.util.render.shader.render.Type;
import lord.daniel.alexander.util.render.shader.render.ingame.ShaderRenderer;
import lord.daniel.alexander.util.render.shader.util.FramebufferHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

@Info(frag = "/fragment/bloom.glsl")
public class BloomShader extends ShaderContainer {

    private ShadowModule shadowModule;

    public ShaderProgram shaderProgram = new ShaderProgram(vert, frag, ShaderType.GLSL);

    private Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    @Override
    public void reload() {
        this.status = -1;
        if (mc.displayWidth != input.framebufferWidth || mc.displayHeight != input.framebufferHeight) {
            input.deleteFramebuffer();
            input = FramebufferHelper.doFrameBuffer(input);

            output.deleteFramebuffer();
            output = FramebufferHelper.doFrameBuffer(output);
        } else {
            input.framebufferClear();
            output.framebufferClear();
        }
        input.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        output.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void doRender(ShaderRenderType type, Type renderType, List<ShaderRenderer.IShaderAccess> runnables) {
        if(shadowModule == null)
            shadowModule = ModuleStorage.getModuleStorage().getByClass(ShadowModule.class);

        this.reload();

        if (!shadowModule.showIfDisplayIsInactive.getValue()) {
            if (!Display.isVisible() || !Display.isActive()) {
                return;
            }
        }

        if (runnables.isEmpty())
            status = -1;
        else
            status = 1;

        if (status == 1) {
            this.input.bindFramebuffer(true);

            runnables.forEach(IShaderAccess -> IShaderAccess.run(true));

            this.output.bindFramebuffer(true);
            this.shaderProgram.initShader();

            int radius = shadowModule.radius.getValue();

            final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);

            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(calculateGaussianValue(i, radius / 2f));
            }

            weightBuffer.rewind();
            setupUniforms(radius, weightBuffer);
            shaderProgram.setUniformf("direction", 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            input.bindFramebufferTexture();
            FramebufferQuads.drawQuad();
            mc.getFramebuffer().bindFramebuffer(true);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            shaderProgram.setUniformf("direction", 0.0f, 1.0f);
            output.bindFramebufferTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE16);
            input.bindFramebufferTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            FramebufferQuads.drawQuad();
            GlStateManager.disableBlend();

            shaderProgram.deleteShader();
        }
    }

    private void setupUniforms(float radius, FloatBuffer buffer) {
        shaderProgram.setUniformi("inTexture", 0);
        shaderProgram.setUniformi("textureToCheck", 16);
        shaderProgram.setUniformf("radius", radius);
        shaderProgram.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        OpenGlHelper.glUniform1(shaderProgram.getUniform("weights"), buffer);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

}
