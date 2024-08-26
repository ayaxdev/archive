package lord.daniel.alexander.util.render.shader.util;

import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class FramebufferHelper implements Methods {

    public static Framebuffer doFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static void renderFramebufferScreen(final Framebuffer framebuffer) {
        if (mc.gameSettings.ofFastRender) return;
        ScaledResolution sr = new ScaledResolution(mc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(0, 0);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(0, sr.getScaledHeight());
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(sr.getScaledWidth(), 0);
        }
        GL11.glEnd();
    }


}