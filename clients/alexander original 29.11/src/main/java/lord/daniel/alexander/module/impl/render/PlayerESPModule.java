package lord.daniel.alexander.module.impl.render;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.Render2DEvent;
import lord.daniel.alexander.event.impl.game.Render3DEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ClientColorValue;
import lord.daniel.alexander.util.math.interpolation.InterpolationUtil;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Written by Daniel. on 01/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "PlayerESP", enumModuleType = EnumModuleType.RENDER)
public class PlayerESPModule extends AbstractModule {

    private final ExpandableValue global = new ExpandableValue("Global", this);
    private final MultiSelectValue espModes = new MultiSelectValue("ESPs", this, new String[]{"2D"}, new String[]{"2D", "Box"}).addExpandableParents(global);
    private final ExpandableValue csgo = new ExpandableValue("2D", this).addVisibleCondition(() -> espModes.is("2D"));
    private final BooleanValue border = new BooleanValue("Bordered", this, true).addExpandableParents(csgo);
    private final NumberValue<Float> lineWidth = new NumberValue<>("LineWidth", this, 0.5f, 0.5f, 3f).addExpandableParents(csgo);
    private final BooleanValue renderHealth = new BooleanValue("RenderHealth", this, true).addExpandableParents(csgo);
    private final ClientColorValue healthColor = new ClientColorValue("HealthColor", this, new Color(0, 204, 0), false, true).addVisibleCondition(renderHealth).addExpandableParents(csgo);
    private final BooleanValue renderArmor = new BooleanValue("RenderArmor", this, true).addExpandableParents(csgo);
    private final ClientColorValue armorColor = new ClientColorValue("ArmorColor", this, new Color(0, 102, 204), false, true).addVisibleCondition(renderHealth).addExpandableParents(csgo);
    private final ExpandableValue box = new ExpandableValue("Box", this).addVisibleCondition(() -> espModes.is("Box"));
    private final ClientColorValue boxColor = new ClientColorValue("BoxColor", this).addExpandableParents(box);

    private final Frustum frustum = new Frustum();

    @EventLink
    public final Listener<Render2DEvent> render2DEventListener = render2DEvent -> {
        frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
        
        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (((entity != mc.thePlayer && mc.gameSettings.thirdPersonView == 0) &&
                    entity instanceof EntityPlayer ||
                    entity instanceof EntityPlayer &&
                            mc.gameSettings.thirdPersonView != 0) &&
                    frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) {

                final double[] coords = new double[4];
                InterpolationUtil.convertBox(coords, entity);
                float x = (float) coords[0];
                float y = (float) coords[1];
                float width = (float) (coords[2]);
                float height = (float) (coords[3]);

                if(espModes.is("2D")) {
                    RenderUtil.drawBorder(x, y, width, height, lineWidth.getValue(), -1, true);

                    if(border.getValue()) {
                        RenderUtil.drawBorder(x - lineWidth.getValue(), y - lineWidth.getValue(), width + lineWidth.getValue(), height + lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);
                        RenderUtil.drawBorder(x + lineWidth.getValue(), y + lineWidth.getValue(), width - lineWidth.getValue(), height - lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);
                    }

                    if(renderHealth.getValue()) {
                        if(border.getValue())
                            RenderUtil.drawBorder(x - lineWidth.getValue() * 5, y - lineWidth.getValue(), (x - lineWidth.getValue() * 5) + lineWidth.getValue() * 3, height + lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);

                        float barHeightCalc = y + (height - y) * ((EntityPlayer) entity).getHealth() / ((EntityPlayer) entity).getMaxHealth(),
                                barHeight = y + (height - y);

                        Gui.drawRect(x - lineWidth.getValue() * 4, y + barHeight - barHeightCalc, (x - lineWidth.getValue() * 4) + lineWidth.getValue(), barHeight, healthColor.getValue().getRGB());
                    }

                    if(renderArmor.getValue()) {
                        if(border.getValue())
                            RenderUtil.drawBorder(x, height + lineWidth.getValue() * 3, width, height + lineWidth.getValue() * 6, lineWidth.getValue(), Color.black.getRGB(), true);
                        Gui.drawRect(x + lineWidth.getValue(), height + lineWidth.getValue() * 4, x + lineWidth.getValue() + Math.max((width - lineWidth.getValue() * 2 - x) * ((EntityPlayer) entity).getTotalArmorValue() / 20.0F, 0), height + lineWidth.getValue() * 5, armorColor.getValue().getRGB());
                    }
                }
            }
        });
    };

    @EventLink
    public final Listener<Render3DEvent> render3DEventListener = render3DEvent -> {
        frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (((entity != mc.thePlayer && mc.gameSettings.thirdPersonView == 0) &&
                    entity instanceof EntityPlayer ||
                    entity instanceof EntityPlayer &&
                            mc.gameSettings.thirdPersonView != 0) &&
                    frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) {
                if(espModes.is("Box")) {
                    final int color = boxColor.getValue().getRGB();
                    final float red = (float)(color >> 16 & 255) / 255.0F;
                    final float green = (float)(color >> 8 & 255) / 255.0F;
                    final float blue = (float)(color & 255) / 255.0F;
                    GL11.glColor4f(red, green, blue, boxColor.getValue().getAlpha() / 255f);
                    final double x = ((entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * render3DEvent.getPartialTicks()) - mc.getRenderManager().renderPosX);
                    final double y = ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * render3DEvent.getPartialTicks()) - mc.getRenderManager().renderPosY);
                    final double z = ((entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * render3DEvent.getPartialTicks()) - mc.getRenderManager().renderPosZ);
                    RenderUtil.drawBox(x + entity.width * 1.5 / 2, y, z + entity.width * 1.5 / 2, entity.width * -1.5, entity.height + 0.2);
                }
            }
        });
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
