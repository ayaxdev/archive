package lord.daniel.alexander.util.math.interpolation;

import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.util.glu.GLU;

/**
 * Written by Daniel. on 01/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public final class InterpolationUtil implements Methods {

	public static AxisAlignedBB interpolateRenderBB(final Entity entity) {
		final double minX = entity.lastTickPosX - entity.width / 2. + (entity.posX - entity.lastTickPosX) * mc.timer.partialTicks - mc.getRenderManager().renderPosX,
				minY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.partialTicks - mc.getRenderManager().renderPosY,
				minZ = entity.lastTickPosZ - entity.width / 2. + (entity.posZ - entity.lastTickPosZ) * mc.timer.partialTicks - mc.getRenderManager().renderPosZ,
				maxX = entity.lastTickPosX + entity.width / 2. + (entity.posX - entity.lastTickPosX) * mc.timer.partialTicks - mc.getRenderManager().renderPosX,
				maxY = entity.lastTickPosY + entity.height + (entity.posY - entity.lastTickPosY) * mc.timer.partialTicks - mc.getRenderManager().renderPosY,
				maxZ = entity.lastTickPosZ + entity.width / 2. + (entity.posZ - entity.lastTickPosZ) * mc.timer.partialTicks - mc.getRenderManager().renderPosZ;
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static double interpolate(final double previous, final double current, final double scale) {
		return previous + (current - previous) * scale;
	}

	public static double interpolate(final double previous, final double current) {
		return previous + (current - previous) * mc.timer.renderPartialTicks;
	}

	public static float interpolate(final float previous, final float current, final float scale) {
		return previous + (current - previous) * scale;
	}

	public static void convertBox(double[] coordsOut, AxisAlignedBB axisAlignedBB) {
		final ScaledResolution scaledResolution = new ScaledResolution(mc);
		coordsOut[0] = Double.MAX_VALUE;
		coordsOut[1] = Double.MAX_VALUE;
		coordsOut[2] = Double.MIN_VALUE;
		coordsOut[3] = Double.MIN_VALUE;
		for (double x = axisAlignedBB.minX; x <= axisAlignedBB.maxX; x += axisAlignedBB.maxX - axisAlignedBB.minX) {
			for (double z = axisAlignedBB.minZ; z <= axisAlignedBB.maxZ; z += axisAlignedBB.maxZ - axisAlignedBB.minZ) {
				for (double y = axisAlignedBB.minY; y <= axisAlignedBB.maxY; y += axisAlignedBB.maxY - axisAlignedBB.minY) {
					coordsOut[0] = Math.min(coordsOut[0], convertCoord(x, y, z, scaledResolution)[0]);
					coordsOut[1] = Math.min(coordsOut[1], scaledResolution.getScaledHeight_double() - convertCoord(x, y, z, scaledResolution)[1]);
					coordsOut[2] = Math.max(coordsOut[2], convertCoord(x, y, z, scaledResolution)[0]);
					coordsOut[3] = Math.max(coordsOut[3], scaledResolution.getScaledHeight_double() - convertCoord(x, y, z, scaledResolution)[1]);
				}
			}
		}
	}

	public static void convertBox(double[] coordsOut, Entity entity) {
		final AxisAlignedBB interpolatedBB = interpolateRenderBB(entity);
		final ScaledResolution scaledResolution = new ScaledResolution(mc);
		coordsOut[0] = Double.MAX_VALUE;
		coordsOut[1] = Double.MAX_VALUE;
		coordsOut[2] = Double.MIN_VALUE;
		coordsOut[3] = Double.MIN_VALUE;
		for (double x = interpolatedBB.minX; x <= interpolatedBB.minX + entity.width; x += entity.width) {
			for (double z = interpolatedBB.minZ; z <= interpolatedBB.minZ + entity.width; z += entity.width) {
				for (double y = interpolatedBB.minY; y <= interpolatedBB.minY + entity.height; y += entity.height) {
					coordsOut[0] = Math.min(coordsOut[0], convertCoord(x, y, z, scaledResolution)[0]);
					coordsOut[1] = Math.min(coordsOut[1], scaledResolution.getScaledHeight_double() - convertCoord(x, y, z, scaledResolution)[1]);
					coordsOut[2] = Math.max(coordsOut[2], convertCoord(x, y, z, scaledResolution)[0]);
					coordsOut[3] = Math.max(coordsOut[3], scaledResolution.getScaledHeight_double() - convertCoord(x, y, z, scaledResolution)[1]);
				}
			}
		}
	}

	public static void convertBox(double[] coordsOut, TileEntity entity) {
		final AxisAlignedBB interpolatedBB = mc.theWorld.getBlockState(entity.getPos()).getBlock().getSelectedBoundingBox(mc.theWorld, entity.getPos()).offset(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);
		final ScaledResolution scaledResolution = new ScaledResolution(mc);
		coordsOut[0] = Double.MAX_VALUE;
		coordsOut[1] = Double.MAX_VALUE;
		coordsOut[2] = Double.MIN_VALUE;
		coordsOut[3] = Double.MIN_VALUE;
		for (double x = interpolatedBB.minX; x <= interpolatedBB.maxX; x += interpolatedBB.maxX - interpolatedBB.minX) {
			for (double z = interpolatedBB.minZ; z <= interpolatedBB.maxZ; z += interpolatedBB.maxZ - interpolatedBB.minZ) {
				for (double y = interpolatedBB.minY; y <= interpolatedBB.maxY; y += interpolatedBB.maxY - interpolatedBB.minY) {
					coordsOut[0] = Math.min(coordsOut[0], convertCoord(x, y, z, scaledResolution)[0]);
					coordsOut[1] = Math.min(coordsOut[1], scaledResolution.getScaledHeight_double() - convertCoord(x, y, z, scaledResolution)[1]);
					coordsOut[2] = Math.max(coordsOut[2], convertCoord(x, y, z, scaledResolution)[0]);
					coordsOut[3] = Math.max(coordsOut[3], scaledResolution.getScaledHeight_double() - convertCoord(x, y, z, scaledResolution)[1]);
				}
			}
		}
	}

	private static double[] convertCoord(double x, double y, double z, final ScaledResolution scaledResolution) {
		final boolean success = GLU.gluProject((float) x, (float) y, (float) z,
				ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS);
		if (success)
			return new double[]{ActiveRenderInfo.OBJECTCOORDS.get(0) / scaledResolution.getScaleFactor(),
					ActiveRenderInfo.OBJECTCOORDS.get(1) / scaledResolution.getScaleFactor()};
		return new double[0];
	}

}