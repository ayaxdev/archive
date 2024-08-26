package lord.daniel.alexander.util.render.shader.render;

import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class MCFramebufferRender implements Methods {

    public static void renderFramebuffer(final Framebuffer framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        framebuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
    }

}
