package net.minecraft.client.renderer.entity;

import java.util.Random;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.event.impl.ItemRenderEvent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItem extends Render<EntityItem>
{
    private final RenderItem itemRenderer;
    private Random field_177079_e = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_)
    {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int transformModelCount(EntityItem itemIn, double x, double y, double z, float spinQuality, IBakedModel bakedModel) {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null) {
            return 0;
        } else {
            boolean gui3d = bakedModel.isGui3d();
            int modelCount = this.getModelCount(itemstack);
            float spinAmount = MathHelper.sin(((float)itemIn.getAge() + spinQuality) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
            float yScale = bakedModel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)x, (float)y + spinAmount + 0.25F * yScale, (float)z);

            if (gui3d || this.renderManager.options != null) {
                float rotationAngle = (((float)itemIn.getAge() + spinQuality) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                GlStateManager.rotate(rotationAngle, 0.0F, 1.0F, 0.0F);
            }

            if (!gui3d) {
                float rotationX = -0.0F * (float)(modelCount - 1) * 0.5F;
                float rotationY = -0.0F * (float)(modelCount - 1) * 0.5F;
                float rotationZ = -0.046875F * (float)(modelCount - 1) * 0.5F;

                ItemRenderEvent itemRenderEvent = new ItemRenderEvent(item, rotationX, rotationY, rotationZ);
                AtaniClient.getInstance().eventPubSub.publish(itemRenderEvent);

                if(!itemRenderEvent.cancelled && itemRenderEvent.shouldScale) {
                    GlStateManager.scale(itemRenderEvent.scale, itemRenderEvent.scale, itemRenderEvent.scale);
                }

                GlStateManager.translate(itemRenderEvent.rotationX, itemRenderEvent.rotationY, itemRenderEvent.rotationZ);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return modelCount;
        }
    }

    private int getModelCount(ItemStack stack)
    {
        int i = 1;

        if (stack.stackSize > 48)
        {
            i = 5;
        }
        else if (stack.stackSize > 32)
        {
            i = 4;
        }
        else if (stack.stackSize > 16)
        {
            i = 3;
        }
        else if (stack.stackSize > 1)
        {
            i = 2;
        }

        return i;
    }

    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        ItemStack itemstack = entity.getEntityItem();
        this.field_177079_e.setSeed(187L);
        boolean flag = false;

        if (this.bindEntityTexture(entity))
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
        int i = this.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);

        for (int j = 0; j < i; ++j)
        {
            if (ibakedmodel.isGui3d())
            {
                GlStateManager.pushMatrix();

                if (j > 0)
                {
                    float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f1 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(f, f1, f2);
                }

                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();
                ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
                float f3 = ibakedmodel.getItemCameraTransforms().ground.scale.x;
                float f4 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
                float f5 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
                GlStateManager.translate(0.0F * f3, 0.0F * f4, 0.046875F * f5);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (flag)
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityItem entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}
