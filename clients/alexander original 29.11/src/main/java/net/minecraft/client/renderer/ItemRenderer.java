package net.minecraft.client.renderer;

import lord.daniel.alexander.module.impl.render.BlockAnimationModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class ItemRenderer
{
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /** A reference to the Minecraft object. */
    private final Minecraft mc;
    private ItemStack itemToRender;

    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;

    /** The index of the currently held item (0-8, or -1 if not yet updated) */
    private int equippedItemSlot = -1;

    public ItemRenderer(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform)
    {
        if (heldStack != null)
        {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();

            if (this.itemRenderer.shouldRenderItemIn3D(heldStack))
            {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (this.isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask))
                {
                    GlStateManager.depthMask(false);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (this.isBlockTranslucent(block))
            {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Returns true if given block is translucent
     */
    private boolean isBlockTranslucent(Block blockIn)
    {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void func_178101_a(float angle, float p_178101_2_)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(p_178101_2_, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void func_178109_a(AbstractClientPlayer clientPlayer)
    {
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);

        if (Config.isDynamicLights())
        {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }

        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks)
    {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private float func_178100_c(float p_178100_1_)
    {
        float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
        return f;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer)
    {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render<AbstractClientPlayer> render = this.renderManager.<AbstractClientPlayer>getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer)render;

        if (!clientPlayer.isInvisible())
        {
            GlStateManager.disableCull();
            this.renderRightArm(renderplayer);
            this.renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_)
    {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float)Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float)Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(p_178097_4_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        float f3 = this.func_178100_c(p_178097_2_);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, p_178097_3_ * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, f3 * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(p_178097_4_ * p_178097_4_ * (float)Math.PI);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float)Math.PI);
        GlStateManager.rotate(f4 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f5 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(-7.0D, 135.0D, 0.0D).func_181673_a(0.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, 135.0D, 0.0D).func_181673_a(1.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, -7.0D, 0.0D).func_181673_a(1.0D, 0.0D).endVertex();
        worldrenderer.pos(-7.0D, -7.0D, 0.0D).func_181673_a(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);

        if (mapdata != null)
        {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }
    }

    private void func_178095_a(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_)
    {
        float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float)Math.PI);
        float f1 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float)Math.PI * 2.0F);
        float f2 = -0.4F * MathHelper.sin(p_178095_3_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178095_2_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f3 = MathHelper.sin(p_178095_3_ * p_178095_3_ * (float)Math.PI);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float)Math.PI);
        GlStateManager.rotate(f4 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render<AbstractClientPlayer> render = this.renderManager.<AbstractClientPlayer>getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderplayer = (RenderPlayer)render;
        renderplayer.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    private void func_178105_d(float p_178105_1_)
    {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float)Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float)Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(p_178105_1_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    private void func_178104_a(AbstractClientPlayer clientPlayer, float p_178104_2_)
    {
        float f = (float)clientPlayer.getItemInUseCount() - p_178104_2_ + 1.0F;
        float f1 = f / (float)this.itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);

        if (f1 >= 0.8F)
        {
            f2 = 0.0F;
        }

        GlStateManager.translate(0.0F, f2, 0.0F);
        float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
        GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Performs transformations prior to the rendering of a held item in first person.
     *  
     * @param equipProgress The progress of the animation to equip (raise from out of frame) while switching held items.
     * @param swingProgress The progress of the arm swing animation.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void func_178098_a(float p_178098_1_, AbstractClientPlayer clientPlayer)
    {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float f = (float)this.itemToRender.getMaxItemUseDuration() - ((float)clientPlayer.getItemInUseCount() - p_178098_1_ + 1.0F);
        float f1 = f / 20.0F;
        f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        if (f1 > 0.1F)
        {
            float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
            float f3 = f1 - 0.1F;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
        }

        GlStateManager.translate(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
    }

    private void func_178103_d()
    {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private BlockAnimationModule blockAnimationModule;

    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     *  
     * @param partialTicks The amount of time passed during the current tick, ranging from 0 to 1.
     */
    public void renderItemInFirstPerson(float partialTicks)
    {
        if(blockAnimationModule == null) {
            blockAnimationModule = ModuleStorage.getModuleStorage().getByClass(BlockAnimationModule.class);
        }

        if (!Config.isShaders() || !Shaders.isSkipRenderHand())
        {
            float equipProgress = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
            float swingProgress = abstractclientplayer.getSwingProgress(partialTicks);
            float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
            float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
            this.func_178101_a(f2, f3);
            this.func_178109_a(abstractclientplayer);
            this.func_178110_a((EntityPlayerSP)abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();

            if (this.itemToRender != null)
            {
                if (this.itemToRender.getItem() instanceof ItemMap)
                {
                    this.renderItemMap(abstractclientplayer, f2, equipProgress, swingProgress);
                }
                else if (abstractclientplayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction = this.itemToRender.getItemUseAction();

                    switch (enumaction)
                    {
                        case NONE:
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            break;

                        case EAT:
                        case DRINK:
                            this.func_178104_a(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            break;

                        case BLOCK: {
                            if (!blockAnimationModule.isEnabled()) {
                                this.transformFirstPersonItem(equipProgress, 0.0F);
                                this.func_178103_d();
                            } else {
                                final String mode = blockAnimationModule.animation.getValue();
                                final float funny = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                switch (mode) {
                                    case "None": {
                                        this.transformFirstPersonItem(equipProgress, 0.0F);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "1.7": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Skidding": {
                                        GlStateManager.translate(0.56F - funny / 15, -0.4F + funny / 15, -0.71999997F);
                                        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                                        GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);

                                        final float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                        final float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(f * -30.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(f1 * -85.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.scale(0.25F, 0.25F, 0.25F);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Throw": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress);
                                        this.func_178103_d();
                                        GlStateManager.translate(-0.3, 0.3F, 0);
                                        break;
                                    }

                                    case "Exhibition": {
                                        this.transformFirstPersonItem(equipProgress / 2.0F, 0.0F);
                                        GlStateManager.translate(0.0F, 0.3F, -0.0F);
                                        GlStateManager.rotate(-funny * 31.0F, 1, 0, 2.0F);
                                        GlStateManager.rotate(-funny * 33.0F, 1.5F, (funny / 1.1F), 0F);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Exhibition 2": {
                                        this.transformFirstPersonItem(equipProgress / 2.0F, 0.0f);
                                        GlStateManager.translate(0.0F, 0.3F, -0.0F);
                                        GlStateManager.rotate(-funny * 30.0F, 1, 0, 2.0F);
                                        GlStateManager.rotate(-funny * 44.0F, 1.5F, (funny / 1.2f), 0F);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Spin": {
                                        this.transformFirstPersonItem(equipProgress, 0.0F);
                                        GlStateManager.translate(0, 0.2F, -1);
                                        GlStateManager.rotate(-59, -1, 0, 3);
                                        GlStateManager.rotate(-(System.currentTimeMillis() / 2 % 360), 1, 0, 0.0F);
                                        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                        break;
                                    }

                                    case "Forward": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress - 1);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Old": {
                                        GlStateManager.translate(0, 0.18F, 0);
                                        this.transformFirstPersonItem(equipProgress / 2.0F, swingProgress);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Dortware 2": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress);

                                        GL11.glTranslatef(-0.35F, 0.1F, 0.0F);
                                        GL11.glTranslatef(-0.05F, -0.1F, 0.1F);

                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Cockless": {
                                        this.transformFirstPersonItem(equipProgress / 1.5F, 0.0F);
                                        this.func_178103_d();
                                        GlStateManager.translate(-0.05F, 0.3F, 0.3F);
                                        GlStateManager.rotate(-funny * 140.0F, 8.0F, 0.0F, 8.0F);
                                        GlStateManager.rotate(funny * 90.0F, 8.0F, 0.0F, 8.0F);
                                        break;
                                    }

                                    case "Swang": {
                                        this.transformFirstPersonItem(equipProgress / 2, swingProgress);
                                        GlStateManager.rotate(funny * 30.0F / 2.0F, -funny, -0.0F, 9.0F);
                                        GlStateManager.rotate(funny * 40.0F, 1.0F, -funny / 2.0F, -0.0F);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Shove": {
                                        float nekker = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
                                        GL11.glTranslated(0.03, -0.055, 0.05);
                                        this.transformFirstPersonItem(equipProgress, 0.0f);
                                        GL11.glTranslatef(0.1f, 0.4f, -0.1f);
                                        GL11.glRotated(-nekker * 42.0f, nekker / 2.0f, 0.0, 9.0);
                                        GL11.glRotated(-nekker * 50.0f, 0.8f, nekker / 2.0f, 0.0);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "LB": {
                                        this.transformFirstPersonItem(equipProgress + 0.15f, swingProgress);
                                        this.func_178103_d();
                                        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
                                        break;
                                    }

                                    case "Chill": {
                                        GlStateManager.translate((0.9F / ((funny + 1.45f) / 5) / 3) + funny / 10, -0.75f - funny / 20, -0.71999997F);
                                        GlStateManager.translate(0.3F, equipProgress * -0.6F, -1.0F);
                                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);

                                        final float x = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                        final float y = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(y * -60.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(x * 5.0F, 1.0F, -1.0F, 0.0F);
                                        GlStateManager.rotate(y * 10.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.scale(0.4F, 0.4F, 0.4F);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Swong": {
                                        this.transformFirstPersonItem(equipProgress / 2.0F, swingProgress);
                                        GlStateManager.rotate(funny * 30.0F, -funny, -0.0F, 9.0F);
                                        GlStateManager.rotate(funny * 40.0F, 1.0F, -funny, -0.0F);
                                        GlStateManager.translate(0, 0.3F, 0);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Smooth": {
                                        final float convertedSwingProgress = (float) Math.sin(Math.sqrt(swingProgress) * Math.PI);

                                        this.transformFirstPersonItem(equipProgress, 0.0F);
                                        final float angle = -convertedSwingProgress * 2;
                                        final float y = -convertedSwingProgress * 2;
                                        GL11.glTranslatef(0, y / 10 + 0.1f, 0);
                                        GL11.glRotatef(y * 10, 0, 1, 0);
                                        GL11.glRotatef(250, 0.2f, 1, -0.6f);
                                        GL11.glRotatef(-10, 1, 0.5f, 1);
                                        GL11.glRotatef(-angle * 20, 1, 0.5f, 1);
                                        break;
                                    }

                                    case "Butter": {
                                        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                                        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);

                                        final float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                        final float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(f1 * -20.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.scale(0.4F, 0.4F, 0.4F);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Slide": {
                                        GlStateManager.translate(0.56F, -0.46F + funny / 10, -0.71999997F);
                                        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);

                                        final float cock = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                        final float lolxd = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(cock * -10.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(lolxd * -10.0F, 0.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(lolxd * -70.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.scale(0.4F, 0.4F, 0.4F);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Short": {
                                        GlStateManager.translate(0.56F, -0.35F, -0.71999997F);
                                        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                                        GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);

                                        final float fniggar = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                        final float f1niggar = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(fniggar * 0.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(10.0F, -1.5F, 1.0F, 1.0F);
                                        GlStateManager.rotate(f1niggar * -25.0F, 1.0F, 0.1F, 0F);
                                        GlStateManager.scale(0.28F, 0.28F, 0.28F);

                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Push": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress);
                                        GlStateManager.translate(0, -0.1, 0.5F);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Stab": {
                                        GlStateManager.translate(0.6f, 0.3f, -0.6f + -funny * 0.7);
                                        GlStateManager.rotate(6090, 0.0f, 0.0f, 0.1f);
                                        GlStateManager.rotate(6085, 0.0f, 0.1f, 0.0f);
                                        GlStateManager.rotate(6110, 0.1f, 0.0f, 0.0f);
                                        this.transformFirstPersonItem(0.0F, 0.0f);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Small": {
                                        GL11.glTranslated(0.5D, 0.0D, -0.5D);
                                        this.transformFirstPersonItem(equipProgress - 0.2f, swingProgress);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Dortware": {
                                        final float var1 = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI - 3));
                                        final float var = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                                        this.transformFirstPersonItem(equipProgress, 1.0f);

                                        GlStateManager.rotate(-var * 10, 0.0f, 15.0f, 200.0f);
                                        GlStateManager.rotate(-var * 10f, 300.0f, var / 2.0f, 1.0f);

                                        this.func_178103_d();

                                        GL11.glTranslated(2.4, 0.3, 0.5);
                                        GL11.glTranslatef(-2.10f, -0.2f, 0.1f);
                                        GlStateManager.rotate(var1 * 13.0f, -10.0f, -1.4f, -10.0f);

                                        break;
                                    }

                                    case "Wobble": {
                                        GlStateManager.translate((0.9F / ((funny + 1.45f) / 5) / 3) + funny / 100, -0.75f - funny / 20, -0.71999997F);
                                        GlStateManager.translate(0.3F, equipProgress * -0.6F, -1.0F);
                                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);

                                        final float x = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                                        final float y = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(y * -70.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(x * 110.0F, 1.0F, -1.0F, 0.0F);
                                        GlStateManager.rotate(y * 10.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.scale(0.65F, 0.65F, 0.65F);

                                        this.func_178103_d();

                                        break;
                                    }


                                    case "Chungus": {
                                        final float f12 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                                        final float f = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI));
                                        GlStateManager.translate(0, -Math.sin(equipProgress) / 3, 0);
                                        GlStateManager.translate(0.66F, -0.8f, -0.9F);
                                        GlStateManager.translate(0.35F, f12 * -0.17F, -0.4F);
                                        GlStateManager.rotate(55.0F, 0.3F, 1.0F, 0.0F);
                                        GlStateManager.rotate(f * -40.0F, 4.0F, 1.0F, 2.0F);
                                        GlStateManager.rotate(f12 * -20.0F, 0.0F, 4.0F, 1.0F);
                                        GlStateManager.rotate(f12 * -40.0F, 1.0F, 0.0F, 0.0F);
                                        GlStateManager.scale(0.7F, 0.7F, 0.7F);
                                        this.func_178103_d();
                                        break;
                                    }

                                    case "Bitch Slap": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress);

                                        GlStateManager.translate(3, 0, 0F);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Leaked": {
                                        this.transformFirstPersonItem(equipProgress, 0);
                                        GlStateManager.scale(0.8f, 0.8f, 0.8f);
                                        GlStateManager.translate(0, 0.1f, 0);
                                        this.func_178103_d();
                                        GlStateManager.rotate(funny * 35.0f / 2.0f, 0.0f, 1, 1.5f);
                                        GlStateManager.rotate(-funny * 135.0f / 4.0f, 1, 1, 0.0f);

                                        break;
                                    }

                                    case "Rise": {
                                        this.transformFirstPersonItem(equipProgress, swingProgress);

                                        GlStateManager.translate(0.3, 0, -0.3);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Flush": {
                                        final float owo = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                                        GlStateManager.translate(0, -Math.sin(equipProgress) / 3, 0);
                                        GlStateManager.translate(0.56F, -0.4F, -0.71999997F);
                                        GlStateManager.translate(0.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(70.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(owo * -20.0F, 10.0F, 6.0F, 0.0F);

                                        GlStateManager.scale(0.4, 0.4, 0.4);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Whack": {
                                        this.transformFirstPersonItem(equipProgress / 2.0F - 0.18F, 0.0F);

                                        final float swing = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                                        GL11.glRotatef(-swing * 80.0f / 5.0f, swing / 3.0f, -0.0f, 9.0f);
                                        GL11.glRotatef(-swing * 40.0f, 8.0f, swing / 9.0f, -0.1f);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Big Whack": {
                                        this.transformFirstPersonItem(equipProgress + 0.0F, swingProgress);

                                        final float swing = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                                        GL11.glRotatef(-swing * 80.0f / 5.0f, swing / 3.0f, -0.0f, 9.0f);
                                        GL11.glRotatef(-swing * 40.0f, 8.0f, swing / 9.0f, -0.1f);

                                        this.func_178103_d();

                                        break;
                                    }

                                    case "Spinny": {

                                        this.transformFirstPersonItem(equipProgress, swingProgress);
                                        this.func_178103_d();
                                        final float var18 = MathHelper.sin(swingProgress * swingProgress * 5.1415925f);
                                        GL11.glTranslatef(0.1f, -0.1f, 0.3f);
                                        GlStateManager.translate(0.1f, -0.1f, 0.4f);
                                        GlStateManager.rotate(var18 * 360, 215.0f, 1.4f, 525.0f);

                                        break;
                                    }

                                    case "Reverse": {

                                        this.transformFirstPersonItem(equipProgress, 1.0f);
                                        this.func_178103_d();
                                        GL11.glTranslatef(0.4f, 0.1f, -0.01f);
                                        final float var16 = MathHelper.sin(swingProgress * swingProgress * 5.1415925f);
                                        GlStateManager.translate(-0.1f, 0.3f, 0.2f);
                                        GlStateManager.rotate(var16 * 13.0f, -10.0f, -1.4f, -10.0f);

                                        break;
                                    }
                                    case "Down":
                                        GlStateManager.translate(0.56F - swingProgress / 20, -0.40F + swingProgress / 40, -0.71999997F);
                                        GlStateManager.translate(0.0F, swingProgress * -0.6F, 0.0F - swingProgress / 25);
                                        GlStateManager.rotate(33.0F, 0.0F, 1.0F, 0.0F);

                                        final float f = MathHelper.sin(equipProgress * equipProgress * (float) Math.PI);
                                        final float f1 = MathHelper.sin(MathHelper.sqrt_float(equipProgress) * (float) Math.PI);

                                        GlStateManager.rotate(f * 0.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(f1 * -swingProgress, 1.0F, 0.2F, 1.0F);
                                        GlStateManager.rotate(-10, 1, 0, 0);
                                        GlStateManager.rotate(f1 * -swingProgress, 1F, 0.4F, 0.9F);
                                        GlStateManager.scale(0.4F, 0.4F, 0.4F);

                                        this.func_178103_d();
                                        break;

                                    case "Inwards":
                                        this.transformFirstPersonItem(equipProgress, swingProgress);
                                        GlStateManager.translate(0.05F, 0.2F, 0.05F);
                                        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                                        GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
                                        break;

                                    case "Rhys": {
                                        GlStateManager.translate(0.41F, -0.25F, -0.5555557F);
                                        GlStateManager.translate(0.0F, 0, 0.0F);
                                        GlStateManager.rotate(35.0F, 0f, 1.5F, 0.0F);

                                        GlStateManager.translate(0, -Math.sin(equipProgress) / 3, 0);
                                        final float lololol = MathHelper.sin(swingProgress * swingProgress / 64 * (float) Math.PI);
                                        final float no = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                                        GlStateManager.rotate(lololol * -5.0F, 0.0F, 0.0F, 0.0F);
                                        GlStateManager.rotate(no * -12.0F, 0.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(no * -65.0F, 1.0F, 0.0F, 0.0F);

                                        GlStateManager.scale(0.3F, 0.3F, 0.3F);
                                        this.func_178103_d();

                                        break;
                                    }
                                }
                            }

                            break;
                        }


                        case BOW:
                            this.transformFirstPersonItem(equipProgress, 0.0F);
                            this.func_178098_a(partialTicks, abstractclientplayer);
                    }
                }
                else
                {
                    this.func_178105_d(swingProgress);
                    this.transformFirstPersonItem(equipProgress, swingProgress);
                }

                this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            }
            else if (!abstractclientplayer.isInvisible())
            {
                this.func_178095_a(abstractclientplayer, equipProgress, swingProgress);
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }

    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(float partialTicks)
    {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock())
        {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayer entityplayer = this.mc.thePlayer;

            for (int i = 0; i < 8; ++i)
            {
                double d0 = entityplayer.posX + (double)(((float)((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
                double d1 = entityplayer.posY + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
                double d2 = entityplayer.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double)entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);

                if (iblockstate1.getBlock().isVisuallyOpaque())
                {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

            if (iblockstate.getBlock().getRenderType() != -1)
            {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);

                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, new Object[] {this.mc.thePlayer, Float.valueOf(partialTicks), object, iblockstate, blockpos}))
                {
                    this.func_178108_a(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator())
        {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, new Object[] {this.mc.thePlayer, Float.valueOf(partialTicks)}))
            {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, new Object[] {this.mc.thePlayer, Float.valueOf(partialTicks)}))
            {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    private void func_178108_a(float p_178108_1_, TextureAtlasSprite p_178108_2_)
    {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.1F;
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;
        float f6 = p_178108_2_.getMinU();
        float f7 = p_178108_2_.getMaxU();
        float f8 = p_178108_2_.getMinV();
        float f9 = p_178108_2_.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(-1.0D, -1.0D, -0.5D).func_181673_a((double)f7, (double)f9).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -0.5D).func_181673_a((double)f6, (double)f9).endVertex();
        worldrenderer.pos(1.0D, 1.0D, -0.5D).func_181673_a((double)f6, (double)f8).endVertex();
        worldrenderer.pos(-1.0D, 1.0D, -0.5D).func_181673_a((double)f7, (double)f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     */
    private void renderWaterOverlayTexture(float p_78448_1_)
    {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay())
        {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = this.mc.thePlayer.getBrightness(p_78448_1_);
            GlStateManager.color(f, f, f, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            float f1 = 4.0F;
            float f2 = -1.0F;
            float f3 = 1.0F;
            float f4 = -1.0F;
            float f5 = 1.0F;
            float f6 = -0.5F;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
            worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
            worldrenderer.pos(-1.0D, -1.0D, -0.5D).func_181673_a((double)(4.0F + f7), (double)(4.0F + f8)).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -0.5D).func_181673_a((double)(0.0F + f7), (double)(4.0F + f8)).endVertex();
            worldrenderer.pos(1.0D, 1.0D, -0.5D).func_181673_a((double)(0.0F + f7), (double)(0.0F + f8)).endVertex();
            worldrenderer.pos(-1.0D, 1.0D, -0.5D).func_181673_a((double)(4.0F + f7), (double)(0.0F + f8)).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson(float p_78442_1_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0F - f) / 2.0F;
            float f6 = f5 + f;
            float f7 = 0.0F - f / 2.0F;
            float f8 = f7 + f;
            float f9 = -0.5F;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
            worldrenderer.setSprite(textureatlassprite);
            worldrenderer.pos((double)f5, (double)f7, (double)f9).func_181673_a((double)f2, (double)f4).endVertex();
            worldrenderer.pos((double)f6, (double)f7, (double)f9).func_181673_a((double)f1, (double)f4).endVertex();
            worldrenderer.pos((double)f6, (double)f8, (double)f9).func_181673_a((double)f1, (double)f3).endVertex();
            worldrenderer.pos((double)f5, (double)f8, (double)f9).func_181673_a((double)f2, (double)f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem()
    {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayer entityplayer = this.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;

        if (this.itemToRender != null && itemstack != null)
        {
            if (!this.itemToRender.getIsItemStackEqual(itemstack))
            {
                if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists())
                {
                    boolean flag1 = Reflector.callBoolean(this.itemToRender.getItem(), Reflector.ForgeItem_shouldCauseReequipAnimation, new Object[] {this.itemToRender, itemstack, Boolean.valueOf(this.equippedItemSlot != entityplayer.inventory.currentItem)});

                    if (!flag1)
                    {
                        this.itemToRender = itemstack;
                        this.equippedItemSlot = entityplayer.inventory.currentItem;
                        return;
                    }
                }

                flag = true;
            }
        }
        else if (this.itemToRender == null && itemstack == null)
        {
            flag = false;
        }
        else
        {
            flag = true;
        }

        float f2 = 0.4F;
        float f = flag ? 0.0F : 1.0F;
        float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
        this.equippedProgress += f1;

        if (this.equippedProgress < 0.1F)
        {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityplayer.inventory.currentItem;

            if (Config.isShaders())
            {
                Shaders.setItemToRenderMain(itemstack);
            }
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress()
    {
        this.equippedProgress = 0.0F;
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress2()
    {
        this.equippedProgress = 0.0F;
    }
}
