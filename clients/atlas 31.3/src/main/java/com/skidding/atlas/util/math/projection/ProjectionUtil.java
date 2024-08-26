package com.skidding.atlas.util.math.projection;

import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.util.glu.GLU;

public final class ProjectionUtil implements IMinecraft {

    public static double[] projectBox(final AxisAlignedBB axisAlignedBB) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final double[] out = new double[4];

        out[0] = Double.MAX_VALUE;
        out[1] = Double.MAX_VALUE;
        out[2] = Double.MIN_VALUE;
        out[3] = Double.MIN_VALUE;

        for (double x = axisAlignedBB.minX; x <= axisAlignedBB.maxX; x += axisAlignedBB.maxX - axisAlignedBB.minX) {
            for (double z = axisAlignedBB.minZ; z <= axisAlignedBB.maxZ; z += axisAlignedBB.maxZ - axisAlignedBB.minZ) {
                for (double y = axisAlignedBB.minY; y <= axisAlignedBB.maxY; y += axisAlignedBB.maxY - axisAlignedBB.minY) {
                    out[0] = Math.min(out[0], projectCoordinates(x, y, z, scaledResolution)[0]);
                    out[1] = Math.min(out[1], scaledResolution.getScaledHeight_double() - projectCoordinates(x, y, z, scaledResolution)[1]);
                    out[2] = Math.max(out[2], projectCoordinates(x, y, z, scaledResolution)[0]);
                    out[3] = Math.max(out[3], scaledResolution.getScaledHeight_double() - projectCoordinates(x, y, z, scaledResolution)[1]);
                }
            }
        }

        return out;
    }

    public static double[] projectBox(Entity entity) {
        final AxisAlignedBB interpolatedBB = RenderUtil.interpolateBoxForRender(entity);
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final double[] out = new double[4];

        out[0] = Double.MAX_VALUE;
        out[1] = Double.MAX_VALUE;
        out[2] = Double.MIN_VALUE;
        out[3] = Double.MIN_VALUE;

        for (double x = interpolatedBB.minX; x <= interpolatedBB.minX + entity.width; x += entity.width) {
            for (double z = interpolatedBB.minZ; z <= interpolatedBB.minZ + entity.width; z += entity.width) {
                for (double y = interpolatedBB.minY; y <= interpolatedBB.minY + entity.height; y += entity.height) {
                    out[0] = Math.min(out[0], projectCoordinates(x, y, z, scaledResolution)[0]);
                    out[1] = Math.min(out[1], scaledResolution.getScaledHeight_double() - projectCoordinates(x, y, z, scaledResolution)[1]);
                    out[2] = Math.max(out[2], projectCoordinates(x, y, z, scaledResolution)[0]);
                    out[3] = Math.max(out[3], scaledResolution.getScaledHeight_double() - projectCoordinates(x, y, z, scaledResolution)[1]);
                }
            }
        }

        return out;
    }

    public static void projectBox(TileEntity entity) {
        final AxisAlignedBB interpolatedBB = mc.theWorld.getBlockState(entity.getPos()).getBlock().getSelectedBoundingBox(mc.theWorld, entity.getPos()).offset(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final double[] out = new double[4];

        out[0] = Double.MAX_VALUE;
        out[1] = Double.MAX_VALUE;
        out[2] = Double.MIN_VALUE;
        out[3] = Double.MIN_VALUE;

        for (double x = interpolatedBB.minX; x <= interpolatedBB.maxX; x += interpolatedBB.maxX - interpolatedBB.minX) {
            for (double z = interpolatedBB.minZ; z <= interpolatedBB.maxZ; z += interpolatedBB.maxZ - interpolatedBB.minZ) {
                for (double y = interpolatedBB.minY; y <= interpolatedBB.maxY; y += interpolatedBB.maxY - interpolatedBB.minY) {
                    out[0] = Math.min(out[0], projectCoordinates(x, y, z, scaledResolution)[0]);
                    out[1] = Math.min(out[1], scaledResolution.getScaledHeight_double() - projectCoordinates(x, y, z, scaledResolution)[1]);
                    out[2] = Math.max(out[2], projectCoordinates(x, y, z, scaledResolution)[0]);
                    out[3] = Math.max(out[3], scaledResolution.getScaledHeight_double() - projectCoordinates(x, y, z, scaledResolution)[1]);
                }
            }
        }
    }

    private static double[] projectCoordinates(double x, double y, double z, final ScaledResolution scaledResolution) {
        final boolean success = GLU.gluProject((float) x, (float) y, (float) z,
                ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS);
        if (success)
            return new double[]{ActiveRenderInfo.OBJECTCOORDS.get(0) / scaledResolution.getScaleFactor(),
                    ActiveRenderInfo.OBJECTCOORDS.get(1) / scaledResolution.getScaleFactor()};
        return new double[0];
    }

}