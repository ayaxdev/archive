package lord.daniel.alexander.util.render.shader.shaders;

import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.util.render.shader.ShaderProgram;
import lord.daniel.alexander.util.render.shader.util.FramebufferHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

import static org.lwjgl.opengl.GL11.*;

public class ShaderBackground implements Methods {

    private final ResourceLocation resourceLocation;

    public ShaderBackground(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    private ShaderProgram shaderProgram;
    private long initTime = 0L;

    public void init() {
        this.initTime = System.currentTimeMillis();
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(mc.getResourceManager().getResource(resourceLocation).getInputStream());
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final StringJoiner joiner = new StringJoiner("\n");
            String line;
            while ((line = bufferedReader.readLine()) != null)
                joiner.add(line);
            bufferedReader.close();
            inputStreamReader.close();
            String shader = joiner.toString();
            this.shaderProgram = new ShaderProgram("vertex/vertex.vsh", shader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Framebuffer framebuffer;

    public void render() {
        if (!Display.isVisible() || !Display.isActive()) {
            return;
        }

        framebuffer = FramebufferHelper.doFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(false);
        shaderProgram.initShader();
        handleUniforms();
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        FramebufferHelper.renderFramebufferScreen(framebuffer);
        shaderProgram.deleteShader();
    }

    public void handleUniforms() {
        float time = (System.currentTimeMillis() - this.initTime) / 1000f;
        GL20.glUniform1f(shaderProgram.getUniform("time"), time);
        GL20.glUniform2f(shaderProgram.getUniform("resolution"), mc.displayWidth, mc.displayHeight);
    }

}
