package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.event.impl.render.overlay.ShowDebugInfoEvent;
import com.skidding.atlas.font.FontRendererValue;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;
import com.skidding.atlas.util.animation.impl.SmoothStepAnimation;
import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.optifine.util.ArrayUtils;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.NativeMemory;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CleanF3Module extends ModuleFeature {

    public final SettingFeature<Float> animationDuration = slider("Animation duration", 500, 0, 1000, 0).build();
    public final SettingFeature<FontRendererValue> font = font("Font", "Roboto", "Regular", 18).build();
    public final SettingFeature<Boolean> displayWhiteSpaceCharacters = check("Display white space characters", true).build();


    private final Animation animation = new SmoothStepAnimation(200, 100, Direction.BACKWARDS);

    public float leftY, rightY = 0;

    public CleanF3Module() {
        super(new ModuleBuilder("CleanF3", "Shows a modified version of the F3 menu", ModuleCategory.RENDER));
    }

    @EventHandler
    public final void onDebug(ShowDebugInfoEvent showDebugInfoEvent) {
        animation.duration = animationDuration.getValue().intValue();

        if(showDebugInfoEvent.showDebug)
            animation.setDirection(Direction.FORWARDS);
        else
            animation.setDirection(Direction.BACKWARDS);

        showDebugInfoEvent.showDebug = false;

        if(!animation.finished(Direction.BACKWARDS)) {
            renderLeft();
            renderRight();
        } else {
            leftY = 0;
            rightY = 0;
        }
    }

    private void renderLeft() {
        final FontRenderer font = this.font.getValue().fontRenderer();

        final Entity renderEntity = mc.getRenderViewEntity();
        final BlockPos renderEntityBlock = new BlockPos(renderEntity.posX, renderEntity.getEntityBoundingBox().minY, renderEntity.posZ);

        final Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(renderEntityBlock);

        final EnumFacing enumfacing = renderEntity.getHorizontalFacing();

        final String facing = switch (enumfacing) {
            case NORTH -> "Towards negative Z";
            case SOUTH -> "Towards positive Z";
            case WEST -> "Towards negative X";
            case EAST -> "Towards positive X";
            default -> "Invalid";
        };

        DifficultyInstance difficultyinstance = this.mc.theWorld.getDifficultyForLocation(renderEntityBlock);
        if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
            EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());

            if (entityplayermp != null) {
                DifficultyInstance difficultyinstance1 = this.mc.getIntegratedServer().getDifficultyAsync(entityplayermp.worldObj, new BlockPos(entityplayermp));

                if (difficultyinstance1 != null) {
                    difficultyinstance = difficultyinstance1;
                }
            }
        }

        String[] left = new String[] {
                "Client version:",
                STR."Minecraft 1.8.9 (\{this.mc.getVersion()}/\{ClientBrandRetriever.getClientModName()})",
                STR."\{AtlasClient.NAME} Client \{AtlasClient.VERSION} b\{AtlasClient.BUILD_NUMBER}",
                " ",

                "Graphics:",
                STR."FPS: \{Minecraft.getDebugFPS()}/\{Config.getFpsMin()}",
                STR."Smooth FPS: \{Config.isSmoothFps()}",
                STR."Fast renderer: \{Config.isFastRender()}",
                STR."Anisotropic filtering: \{Config.isAnisotropicFiltering()}",
                STR."Anti aliasing: \{Config.isAntialiasing()}",
                STR."Render regions: \{Config.isRenderRegions()}",
                STR."Enabled shaders: \{Config.isShaders()}",
                STR."Shader: \{this.mc.entityRenderer.getShaderGroup() == null ? "None" : this.mc.entityRenderer.getShaderGroup().getShaderGroupName()}",
                " ",


                "Render global:",
                mc.renderGlobal.getDebugInfoRenders(),
                mc.renderGlobal.getDebugInfoEntities(),
                " ",

                "Position:",
                String.format("XYZ: %.3f / %.5f / %.3f", renderEntity.posX, renderEntity.getEntityBoundingBox().minY, renderEntity.posZ),
                String.format("Block: %d %d %d", renderEntityBlock.getX(), renderEntityBlock.getY(), renderEntityBlock.getZ()),
                String.format("Chunk: %d %d %d in %d %d %d", renderEntityBlock.getX() & 15, renderEntityBlock.getY() & 15, renderEntityBlock.getZ() & 15, renderEntityBlock.getX() >> 4, renderEntityBlock.getY() >> 4, renderEntityBlock.getZ() >> 4),
                String.format("Facing: %s (%s) (%.1f / %.1f)", new Object[]{enumfacing, facing, MathHelper.wrapAngleTo180_float(renderEntity.rotationYaw), MathHelper.wrapAngleTo180_float(renderEntity.rotationPitch)}),
                " ",

                "World:",
                STR."Biome: \{chunk.getBiome(renderEntityBlock, this.mc.theWorld.getWorldChunkManager()).biomeName}",
                STR."Light: \{chunk.getLightSubtracted(renderEntityBlock, 0)} (\{chunk.getLightFor(EnumSkyBlock.SKY, renderEntityBlock)} sky, \{chunk.getLightFor(EnumSkyBlock.BLOCK, renderEntityBlock)} block)",
                String.format("Local Difficulty: %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L),
                " ",

                "Mouse over:",
                STR."Object mouse over exists: \{mc.objectMouseOver != null}",
        };

        if(mc.objectMouseOver != null) {
            final String[] mouseOver = new String[] {
                    STR."Type of hit: \{mc.objectMouseOver.typeOfHit.name()}",
                    STR."Entity hit: \{mc.objectMouseOver.entityHit == null ? "None" : mc.objectMouseOver.entityHit.getName()}",
                    STR."Block: \{mc.objectMouseOver.getBlockPos() == null ? "None" : STR."\{renderEntityBlock.getX()} \{renderEntityBlock.getY()} \{renderEntityBlock.getZ()}"}"
            };

            left = (String[]) ArrayUtils.addObjectsToArray(left, mouseOver);
        }

        final String[] finishedLeft = left;
        final double animationOutput = animation.getOutput();

        ShaderRenderer.INSTANCE.drawAndRun(shader -> {
            final float height = finishedLeft.length * (font.getHeight() + 2);;
            float lineY = (float) (0 - (height * (100 - animationOutput)) / 100);

            for(final String line : finishedLeft) {
                if(!line.equalsIgnoreCase(" ") || displayWhiteSpaceCharacters.getValue()) {
                    DrawUtil.drawRectRelative(0, lineY, font.getStringWidth(line) + 2, font.getHeight() + 2, new Color(0, 0, 0, shader ? 255 : 100).getRGB());
                    font.drawStringWithShadow(line, 1, lineY + 1, -1);
                }

                lineY += font.getHeight() + 2;
            }

            leftY = lineY;
        });
    }

    private void renderRight() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final FontRenderer font = this.font.getValue().fontRenderer();

        final long i = Runtime.getRuntime().maxMemory();
        final long j = Runtime.getRuntime().totalMemory();
        final long k = Runtime.getRuntime().freeMemory();
        final long l = j - k;

        final long i1 = NativeMemory.getBufferAllocated();
        final long j1 = NativeMemory.getBufferMaximum();

        final String[] right = new String[] {
                "Java/JVM:",
                String.format("Java Version: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32),
                STR."JVM Name: \{System.getProperty("java.vm.name")}",
                STR."JVM Version: \{System.getProperty("java.vm.version")}",
                String.format("Memory: % 2d%% %03d/%03dMB", l * 100L / i, MathUtil.bytesToMb(l), MathUtil.bytesToMb(i)),
                String.format("Allocated: % 2d%% %03dMB", j * 100L / i, MathUtil.bytesToMb(j)),
                STR."Native: \{MathUtil.bytesToMb(i1)}/\{MathUtil.bytesToMb(j1)}MB",
                STR."GC: \{MemoryMonitor.getAllocationRateMb()}MB/s",
                " ",

                "Hardware/Software Info:",
                STR."Operating System: \{System.getProperty("os.name")}",
                String.format("CPU: %s", OpenGlHelper.getCpu()),
                String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)),
                STR."Renderer: \{GL11.glGetString(GL11.GL_RENDERER)}",
                STR."GL driver version: \{GL11.GL_VERSION}"
        };

        final double animationOutput = animation.getOutput();

        ShaderRenderer.INSTANCE.drawAndRun(shader -> {
            final float height = right.length * (font.getHeight() + 2);
            float lineY = (float) (0 - (height * (100 - animationOutput)) / 100);

            for(final String line : right) {
                if(!line.equalsIgnoreCase(" ") || displayWhiteSpaceCharacters.getValue()) {
                    DrawUtil.drawRectRelative(scaledResolution.getScaledWidth() - (font.getStringWidth(line) + 2), lineY, font.getStringWidth(line) + 2, font.getHeight() + 2, new Color(0, 0, 0, shader ? 255 : 100).getRGB());
                    font.drawStringWithShadow(line, scaledResolution.getScaledWidth() - (font.getStringWidth(line) + 1), lineY + 1, -1);
                }

                lineY += font.getHeight() + 2;
            }

            CleanF3Module.this.rightY = lineY;
        });
    }

    public float getY() {
        return Math.max(leftY, rightY);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
