package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.render.world.NameTagRenderEvent;
import com.skidding.atlas.event.impl.render.overlay.Render2DEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

public class NametagsModule extends ModuleFeature {

    public final SettingFeature<Boolean> includeNonPlayers = check("Include non-players", false).build();

    public NametagsModule() {
        super(new ModuleBuilder("Nametags", "Provides entity identification with rendered nametags", ModuleCategory.RENDER));
    }

    @EventHandler
    public final void onNametagRendered(NameTagRenderEvent nameTagRenderEvent) {
        nameTagRenderEvent.cancelled = true;
    }

    @EventHandler
    public void on2D(Render2DEvent render2DEvent) {
        if(render2DEvent.eventType != Event.EventType.POST)
            return;

        double renderX = mc.getRenderManager().renderPosX, renderY = mc.getRenderManager().renderPosY, renderZ = mc.getRenderManager().renderPosZ;
        int factor = new ScaledResolution(mc).getScaleFactor();
        for (Entity entity : getWorld().loadedEntityList) {
            if (mc.getRenderManager() == null || entity == getPlayer() || entity.isDead || entity.isInvisible() || (entity instanceof EntityArmorStand) || !(entity instanceof EntityLivingBase livingEntity)) {
                continue;
            }

            if (!includeNonPlayers.getValue() && !(entity instanceof EntityPlayer)) {
                continue;
            }

            Vector3f position = project(factor, livingEntity.lastTickPosX - renderX, livingEntity.lastTickPosY - renderY, livingEntity.lastTickPosZ - renderZ);

            if (position != null && position.z >= 0.0D && position.z < 1.0D) {
                float x = position.x / position.z;
                float y = position.y / position.z;

                mc.fontRendererObj.drawStringWithShadow(livingEntity.getName(), x, y, -1);
            }
        }
    }

    private Vector3f project(int factor, double x, double y, double z) {
        if (GLU.gluProject((float) x, (float) y, (float) z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            return new Vector3f((ActiveRenderInfo.OBJECTCOORDS.get(0) / factor), ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS.get(1)) / factor), ActiveRenderInfo.OBJECTCOORDS.get(2));
        }

        return null;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
