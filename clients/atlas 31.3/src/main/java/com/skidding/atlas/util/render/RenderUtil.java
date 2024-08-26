package com.skidding.atlas.util.render;

import com.skidding.atlas.util.minecraft.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

@UtilityClass
public class RenderUtil implements IMinecraft {

    public Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsFramebufferUpdate(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public boolean needsFramebufferUpdate(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static AxisAlignedBB interpolateBoxForRender(final Entity entity) {
        final double minX = entity.lastTickPosX - entity.width / 2. + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                minY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                minZ = entity.lastTickPosZ - entity.width / 2. + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ,
                maxX = entity.lastTickPosX + entity.width / 2. + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                maxY = entity.lastTickPosY + entity.height + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                maxZ = entity.lastTickPosZ + entity.width / 2. + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
