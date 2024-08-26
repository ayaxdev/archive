package com.atani.nextgen.module.impl.render;

import com.atani.nextgen.event.impl.NameTagRenderEvent;
import com.atani.nextgen.event.impl.Render2DEvent;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

public class NametagsModule extends ModuleFeature {

    public NametagsModule() {
        super(new ModuleBuilder("Nametags", "Alter the vanilla minecraft player nametags", ModuleCategory.RENDER));
    }

    @EventHandler
    public final void onNametagRendered(NameTagRenderEvent nameTagRenderEvent) {
        nameTagRenderEvent.cancelled = true;
    }

    @EventHandler
    public void on2D(Render2DEvent render2DEvent) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (mc.getRenderManager() == null || entity == mc.thePlayer || entity.isDead || entity.isInvisible() || (entity instanceof EntityArmorStand) || !(entity instanceof EntityLivingBase livingEntity)) {
                continue;
            }

            mc.fontRendererObj.drawStringWithShadow(livingEntity.getName(), (float) livingEntity.posX, (float) livingEntity.posY, -1);
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
