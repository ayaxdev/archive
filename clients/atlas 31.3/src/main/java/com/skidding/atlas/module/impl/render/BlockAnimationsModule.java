package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.event.impl.render.item.hand.RenderItemUseEvent;
import com.skidding.atlas.event.impl.render.item.hand.RenderSwordUseEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class BlockAnimationsModule extends ModuleFeature {

    public final SettingFeature<String> blockStyle = mode("Block style", "1.7", new String[]{"1.8", "1.7", "Exhibition", "Stab", "Spin", "Komorebi", "Rhys"}).build();
    public final SettingFeature<Boolean> spoof = check("Spoof", false).build();

    public BlockAnimationsModule() {
        super(new ModuleBuilder("BlockAnimations", "Enhances sword blocking animations for a more dynamic look", ModuleCategory.RENDER));
    }

    @EventHandler
    public final void onSwordInUse(RenderSwordUseEvent renderSwordUseEvent) {
        renderSwordUseEvent.cancelled = true;

        final ItemRenderer itemRenderer = renderSwordUseEvent.itemRenderer;
        final float realSwingProgress = renderSwordUseEvent.realSwingProgress;
        final float convertedProgress = MathHelper.sin(MathHelper.sqrt_float(realSwingProgress) * (float) Math.PI);

        switch (blockStyle.getValue()) {
            case "1.8" -> {
                renderSwordUseEvent.cancelled = false;
            }
            case "1.7" -> {
                itemRenderer.transformFirstPersonItem(renderSwordUseEvent.equippedProgress, realSwingProgress);
                itemRenderer.doBlockTransformations();
            }
            case "Exhibition" -> {
                GlStateManager.translate(0, 0.18F, 0);
                float equippedProgress = renderSwordUseEvent.equippedProgress / 2.0f;

                itemRenderer.transformFirstPersonItem(equippedProgress, realSwingProgress);
                itemRenderer.doBlockTransformations();
            }
            case "Stab" -> {
                final float spin = MathHelper.sin(MathHelper.sqrt_float(realSwingProgress) * (float) Math.PI);

                GlStateManager.translate(0.6f, 0.3f, -0.6f + -spin * 0.7);
                GlStateManager.rotate(6090, 0.0f, 0.0f, 0.1f);
                GlStateManager.rotate(6085, 0.0f, 0.1f, 0.0f);
                GlStateManager.rotate(6110, 0.1f, 0.0f, 0.0f);
                itemRenderer.transformFirstPersonItem(0.0F, 0.0f);
                itemRenderer.doBlockTransformations();
            }
            case "Spin" -> {
                itemRenderer.transformFirstPersonItem(realSwingProgress, 0.0F);
                GlStateManager.translate(0, 0.2F, -1);
                GlStateManager.rotate(-59, -1, 0, 3);
                // Don't make the /2 a float it causes the animation to break
                GlStateManager.rotate(-(System.currentTimeMillis() / 2 % 360), 1, 0, 0.0F);
                GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
            }

            case "Komorebi" -> {
                itemRenderer.transformFirstPersonItem(-0.25F, 1.0F + convertedProgress / 10.0F);
                GL11.glRotated(-convertedProgress * 25.0F, 1.0F, 0.0F, 0.0F);
                itemRenderer.doBlockTransformations();
            }

            case "Rhys" -> {
                GlStateManager.translate(0.41F, -0.25F, -0.5555557F);
                GlStateManager.translate(0.0F, 0, 0.0F);
                GlStateManager.rotate(35.0F, 0f, 1.5F, 0.0F);

                final float racism = MathHelper.sin(realSwingProgress * realSwingProgress / 64 * (float) Math.PI);

                GlStateManager.rotate(racism * -5.0F, 0.0F, 0.0F, 0.0F);
                GlStateManager.rotate(convertedProgress * -12.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(convertedProgress * -65.0F, 1.0F, 0.0F, 0.0F);
                itemRenderer.doBlockTransformations();
            }
        }
    }

    @EventHandler
    public final void onRenderItemUse(RenderItemUseEvent renderItemUseEvent) {
        renderItemUseEvent.spoof = spoof.getValue() && getPlayer().isSwingInProgress && getPlayer().getHeldItem() != null && getPlayer().getHeldItem().getItem() instanceof ItemSword;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
