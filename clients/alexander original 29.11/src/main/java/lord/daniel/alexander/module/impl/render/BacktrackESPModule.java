package lord.daniel.alexander.module.impl.render;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.Render2DEvent;
import lord.daniel.alexander.event.impl.game.Render3DEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.module.impl.combat.BacktrackModule;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ClientColorValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.math.interpolation.InterpolationUtil;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Written by Daniel. on 01/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "BacktrackESP", enumModuleType = EnumModuleType.RENDER)
public class BacktrackESPModule extends AbstractModule {

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
    private BacktrackModule backtrackModule;

    @EventLink
    public final Listener<Render2DEvent> render2DEventListener = render2DEvent -> {
        if (backtrackModule == null)
            backtrackModule = ModuleStorage.getModuleStorage().getByClass(BacktrackModule.class);

        frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

        if (backtrackModule.isEnabled() && backtrackModule.getEntity() != null && backtrackModule.isBlockPackets() && frustum.isBoundingBoxInFrustum(backtrackModule.getEntity().getEntityBoundingBox())) {
            EntityLivingBase entity = backtrackModule.getEntity();

            double d0 = entity.realPosX / 32.0;
            double d2 = entity.realPosY / 32.0;
            double d3 = entity.realPosZ / 32.0;
            final float x1 = (float)(d0 - RenderUtil.mc.getRenderManager().getRenderPosX());
            final float y1 = (float)(d2 - RenderUtil.mc.getRenderManager().getRenderPosY());
            final float z1 = (float)(d3 - RenderUtil.mc.getRenderManager().getRenderPosZ());

            AxisAlignedBB alignedBB = new AxisAlignedBB(x1, y1, z1, x1 + entity.width, y1 + entity.height, z1 + entity.width);

            final double[] coords = new double[4];
            InterpolationUtil.convertBox(coords, alignedBB);
            float x = (float) coords[0];
            float y = (float) coords[1];
            float width = (float) (coords[2]);
            float height = (float) (coords[3]);

            if (espModes.is("2D")) {
                RenderUtil.drawBorder(x, y, width, height, lineWidth.getValue(), -1, true);

                if (border.getValue()) {
                    RenderUtil.drawBorder(x - lineWidth.getValue(), y - lineWidth.getValue(), width + lineWidth.getValue(), height + lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);
                    RenderUtil.drawBorder(x + lineWidth.getValue(), y + lineWidth.getValue(), width - lineWidth.getValue(), height - lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);
                }

                if (renderHealth.getValue()) {
                    if (border.getValue())
                        RenderUtil.drawBorder(x - lineWidth.getValue() * 5, y - lineWidth.getValue(), (x - lineWidth.getValue() * 5) + lineWidth.getValue() * 3, height + lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);

                    float barHeightCalc = y + (height - y) * entity.getHealth() / entity.getMaxHealth(),
                            barHeight = y + (height - y);

                    Gui.drawRect(x - lineWidth.getValue() * 4, y + barHeight - barHeightCalc, (x - lineWidth.getValue() * 4) + lineWidth.getValue(), barHeight, healthColor.getValue().getRGB());
                }

                if (renderArmor.getValue()) {
                    if (border.getValue())
                        RenderUtil.drawBorder(x, height + lineWidth.getValue() * 3, width, height + lineWidth.getValue() * 6, lineWidth.getValue(), Color.black.getRGB(), true);
                    Gui.drawRect(x + lineWidth.getValue(), height + lineWidth.getValue() * 4, x + lineWidth.getValue() + Math.max((width - lineWidth.getValue() * 2 - x) * entity.getTotalArmorValue() / 20.0F, 0), height + lineWidth.getValue() * 5, armorColor.getValue().getRGB());
                }
            }
        }
    };

    @EventLink
    public final Listener<Render3DEvent> render3DEventListener = render3DEvent -> {
        if (backtrackModule == null)
            backtrackModule = ModuleStorage.getModuleStorage().getByClass(BacktrackModule.class);

        frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

        if (backtrackModule.isEnabled() && backtrackModule.getEntity() != null && backtrackModule.isBlockPackets() && frustum.isBoundingBoxInFrustum(backtrackModule.getEntity().getEntityBoundingBox())) {
            Entity entity = backtrackModule.getEntity();
            if (espModes.is("Box")) {
                final int color = boxColor.getValue().getRGB();
                final float red = (float) (color >> 16 & 255) / 255.0F;
                final float green = (float) (color >> 8 & 255) / 255.0F;
                final float blue = (float) (color & 255) / 255.0F;
                GL11.glColor4f(red, green, blue, boxColor.getValue().getAlpha() / 255f);
                final double x = entity.realPosX / 32F - mc.getRenderManager().renderPosX;
                final double y = entity.realPosY / 32F - mc.getRenderManager().renderPosY;
                final double z = entity.realPosZ  / 32F- mc.getRenderManager().renderPosZ;
                RenderUtil.drawBox(x + entity.width * 1.5 / 2, y, z + entity.width * 1.5 / 2, entity.width * -1.5, entity.height + 0.2);
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
