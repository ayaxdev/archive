package lord.daniel.alexander.module.impl.render;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.Render3DEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.color.ClientColorValue;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "TNTESP", enumModuleType = EnumModuleType.RENDER)
public class TNTESPModule extends AbstractModule {

    private final ClientColorValue lineColor = new ClientColorValue("LineColor", this, new Color(255, 0, 0, 255), false, true);
    private final ClientColorValue sphereColor = new ClientColorValue("SphereColor", this, new Color(255, 0, 0, 51), false, true);

    private double damage = 0;

    @EventLink
    public final Listener<Render3DEvent> render3DEventListener = render3DEvent -> {
        damage = 0;
        for (Entity entity : getWorld().loadedEntityList) {
            if (entity instanceof final EntityTNTPrimed tnt) {
                final double posX = tnt.posX - mc.getRenderManager().renderPosX;
                final double posY = tnt.posY - mc.getRenderManager().renderPosY;
                final double posZ = tnt.posZ - mc.getRenderManager().renderPosZ;

                GL11.glPushMatrix();
                GL11.glTranslated(posX, posY, posZ);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                RenderUtil.color(sphereColor.getValue());

                Sphere sphere = new Sphere();
                sphere.setDrawStyle(GLU.GLU_FILL);
                sphere.draw(4 * 2, 15, 15);
                float f3 = 4 * 2.0F;
                Vec3 vec3 = new Vec3(tnt.posX, tnt.posY, tnt.posZ);
                if (!getPlayer().isImmuneToExplosions()) {
                    double d12 = getPlayer().getDistance(tnt.posX, tnt.posY, tnt.posZ) / (double) f3;
                    if (d12 <= 1.0D) {
                        double d5 = getPlayer().posX - tnt.posX;
                        double d7 = getPlayer().posY + (double) getPlayer().getEyeHeight() - tnt.posY;
                        double d9 = getPlayer().posZ - tnt.posY;
                        double d13 = MathHelper.sqrt_double(d5 * d5 + d7 * d7 + d9 * d9);
                        if (d13 != 0) {
                            double d14 = getWorld().getBlockDensity(vec3, getPlayer().getEntityBoundingBox());
                            double d10 = (1.0D - d12) * d14;
                            damage += (float) ((int) ((d10 * d10 + d10) / 2.0D * 8.0D * (double) f3 + 1.0D));
                        }
                    }
                }

                RenderUtil.color(lineColor.getValue());

                Sphere lines = new Sphere();
                lines.setDrawStyle(GLU.GLU_LINE);
                lines.draw(4 * 2 + 0.1F, 15, 15);

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
