package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.render.overlay.Render2DEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.math.projection.ProjectionUtil;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class ESPModule extends ModuleFeature {
    public final SettingFeature<String> mode = mode("Mode", "2D", new String[]{"2D"}).build();
    public final SettingFeature<Boolean> bordered = check("Bordered", true).addDependency(mode, "2D").build();
    public final SettingFeature<Float> lineWidth = slider("Line width", 0.5F, 0.5F, 3, 1).addDependency(mode, "2D").build();

    public final SettingFeature<Boolean> renderHealth = check("Render health", true).addDependency(mode, "2D").build();
    public final SettingFeature<Integer> healthColor = color("Health color", 0, 204, 0, 255)
            .addDependency(renderHealth)
            .addDependency(mode, "2D")
            .build();

    public final SettingFeature<Boolean> renderArmor = check("Render armor", false).addDependency(mode, "2D").build();
    public final SettingFeature<Integer> armorColor = color("Armor color", 0, 102, 204, 255)
            .addDependency(renderArmor)
            .addDependency(mode, "2D")
            .build();

    public final SettingFeature<Boolean> blur2D = check("Rect", false).addDependency(mode, "2D").build();
    public final SettingFeature<Integer> blurColor2D = color("Rect color", 0, 0, 0, 20)
            .addDependency(blur2D)
            .addDependency(mode, "2D").build();

    private final Frustum frustum = new Frustum();

    public ESPModule() {
        super(new ModuleBuilder("ESP", "Makes you be able to see entities through walls", ModuleCategory.RENDER));
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if(event.eventType != Event.EventType.POST)
            return;

        switch (mode.getValue()) {
            case "2D" -> {
                frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);

                mc.theWorld.loadedEntityList.forEach(entity -> {
                    if (((entity != mc.thePlayer && mc.gameSettings.thirdPersonView == 0) &&
                            entity instanceof EntityPlayer ||
                            entity instanceof EntityPlayer &&
                                    mc.gameSettings.thirdPersonView != 0) &&
                            frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) {

                        final double[] coords = ProjectionUtil.projectBox(entity);

                        final float x = (float) coords[0];
                        final float y = (float) coords[1];
                        final float width = (float) (coords[2]);
                        final float height = (float) (coords[3]);

                        if (blur2D.getValue()) {
                            ShaderRenderer.INSTANCE.drawAndRun(_ -> DrawUtil.drawRectRelative(x, y, width - x, height - y, blurColor2D.getValue()));
                        }

                        DrawUtil.drawBorderAbsolute(x, y, width, height, lineWidth.getValue(), -1, true);
                        DrawUtil.drawBorderAbsolute(x - lineWidth.getValue(), y - lineWidth.getValue(), width + lineWidth.getValue(), height + lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);
                        DrawUtil.drawBorderAbsolute(x + lineWidth.getValue(), y + lineWidth.getValue(), width - lineWidth.getValue(), height - lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);

                        if(renderHealth.getValue()) {
                            if(bordered.getValue()) {
                                DrawUtil.drawBorderAbsolute(x - lineWidth.getValue() * 5, y - lineWidth.getValue(), (x - lineWidth.getValue() * 5) + lineWidth.getValue() * 3, height + lineWidth.getValue(), lineWidth.getValue(), Color.black.getRGB(), true);
                            }

                            final float barHeightCalc = y + (height - y) * ((EntityPlayer) entity).getHealth() / ((EntityPlayer) entity).getMaxHealth(),
                                    barHeight = y + (height - y);

                            DrawUtil.drawRectAbsolute(x - lineWidth.getValue() * 4, y + barHeight - barHeightCalc, (x - lineWidth.getValue() * 4) + lineWidth.getValue(), barHeight, healthColor.getValue());
                        }

                        if(renderArmor.getValue()) {
                            if(bordered.getValue()) {
                                DrawUtil.drawBorderAbsolute(x, height + lineWidth.getValue() * 3, width, height + lineWidth.getValue() * 6, lineWidth.getValue(), Color.black.getRGB(), true);
                            }
                            DrawUtil.drawRectAbsolute(x + lineWidth.getValue(), height + lineWidth.getValue() * 4, x + lineWidth.getValue() + Math.max((width - lineWidth.getValue() * 2 - x) * ((EntityPlayer) entity).getTotalArmorValue() / 20.0F, 0), height + lineWidth.getValue() * 5, armorColor.getValue());
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
