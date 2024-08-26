package net.jezevcik.argon.utils.math;

import net.jezevcik.argon.system.minecraft.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Final;

import java.math.BigDecimal;

/**
 * A set of methods used for simplifying math operations.
 */
public class MathUtils {

    /**
     * Rounds a number to step, while avoiding floating point issues at the cost of speed.
     *
     * @param input The value to be rounded.
     * @param step The step to which the value will be rounded to.
     * @return The rounded value.
     */
    public static double roundToStepStrict(double input, double step) {
        final BigDecimal fix = BigDecimal.valueOf(Math.round(input / step));
        return fix.multiply(BigDecimal.valueOf(step)).doubleValue();
    }

    /**
     * Rounds a number to step.
     *
     * @param input The value to be rounded.
     * @param step The step to which the value will be rounded to.
     * @return The rounded value.
     */
    public static double roundToStep(double input, double step) {
        return Math.round(input / step) * step;
    }

    /**
     * Rounds to a certain number of decimal places.
     *
     * @param value The value to be rounded.
     * @param places The number of places the value will be rounded to.
     * @return The rounded value.
     */
    public static double roundToPlaces(final double value, final int places) {
        final double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Clamps a method to a minimum and maximum.
     *
     * @param input The value to be clamped.
     * @param min The minimum.
     * @param max The maximum.
     * @return The clamped value.
     */
    public static double clamp(double input, double min, double max) {
        return Math.max(min, Math.min(max, input));
    }

    /**
     * Finds the point in the provided, that is closest to the provided aim vector.
     *
     * @param start The aim vector based on which the point is found.
     * @param box The box, in which the point is going to be found.
     * @return The point in the provided box closest to the aim vector.
     */
    public static Vec3d closestPointInBox(Vec3d start, Box box) {
        final double x = Math.max(box.minX, Math.min(box.maxX, start.x));
        final double y = Math.max(box.minY, Math.min(box.maxY, start.y));
        final double z = Math.max(box.minZ, Math.min(box.maxZ, start.z));
        return new Vec3d(x, y, z);
    }

    /**
     * Finds the distance between 2 points in 3D space.
     *
     * @param x1 The first point's X coordinate.
     * @param y1 The first point's Y coordinate.
     * @param z1 The first point's Z coordinate.
     * @param x2 The second point's X coordinate.
     * @param y2 The second point's Y coordinate.
     * @param z2 The second point's Z coordinate.
     * @return The distance between the 2 points.
     */
    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        final double xDistance = x1 - x2;
        final double yDistance = y1 - y2;
        final double zDistance = z1 - z2;
        return Math.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
    }

    /**
     * Finds the distance between two vectors in a 3D space.
     * @param first The first vector.
     * @param second The second vector.
     * @return The distance between the two vectors.
     */
    public static double getDistance(Vec3d first, Vec3d second) {
        return getDistance(first.x, first.y, first.z, second.x, second.y, second.z);
    }

    public static Vec3d getRotationVector(float yaw, float pitch) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    /**
     * A set of methods used for math regarding colours
     */
    public static class Color {

        public static java.awt.Color[] interpolate(final java.awt.Color firstColor, final java.awt.Color secondColor, int count) {
            final java.awt.Color[] out = new java.awt.Color[count];

            for (int i = 0; i < count; i++) {
                final double percentage = (i / (count - 1d));

                final int firstRed = firstColor.getRed(),
                        firstGreen = firstColor.getGreen(),
                        firstBlue = firstColor.getBlue(),
                        firstAlpha = firstColor.getAlpha();

                final int secondRed = secondColor.getRed(),
                        secondGreen = secondColor.getGreen(),
                        secondBlue = secondColor.getBlue(),
                        secondAlpha = secondColor.getAlpha();

                int interpolatedRed = (int) Math.round(MathHelper.lerp(percentage, firstRed, secondRed)),
                        interpolatedGreen = (int) Math.round(MathHelper.lerp(percentage, firstGreen, secondGreen)),
                        interpolatedBlue = (int) Math.round(MathHelper.lerp(percentage, firstBlue, secondBlue)),
                        interpolatedAlpha = (int) Math.round(MathHelper.lerp(percentage, firstAlpha, secondAlpha));

                interpolatedRed = MathHelper.clamp(interpolatedRed, 0, 255);
                interpolatedGreen = MathHelper.clamp(interpolatedGreen, 0, 255);
                interpolatedBlue = MathHelper.clamp(interpolatedBlue, 0, 255);
                interpolatedAlpha = MathHelper.clamp(interpolatedAlpha, 0, 255);

                out[i] = new java.awt.Color(interpolatedRed, interpolatedGreen, interpolatedBlue, interpolatedAlpha);
            }

            return out;
        }

    }

    /**
     * A set of methods used for raycast math
     */
    public static class RayCast implements Minecraft {

        public static HitResult getHitResult(Entity camera, final float[] rotations, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
            double maxRange = Math.max(blockInteractionRange, entityInteractionRange);
            double squaredRange = MathHelper.square(maxRange);

            final Vec3d cameraPosition = camera.getCameraPosVec(tickDelta);

            final Vec3d cameraRotation = MathUtils.getRotationVector(rotations[0], rotations[1]);
            final Vec3d maximumRangePos = cameraPosition.add(cameraRotation.x * maxRange, cameraRotation.y * maxRange, cameraRotation.z * maxRange);
            final HitResult hitResult =  client.world.raycast(new RaycastContext(cameraPosition, maximumRangePos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, camera));

            double hitResultDistance = hitResult.getPos().squaredDistanceTo(cameraPosition);

            if (hitResult.getType() != net.minecraft.util.hit.HitResult.Type.MISS) {
                squaredRange = hitResultDistance;
                maxRange = Math.sqrt(squaredRange);
            }

            final Vec3d updatedMaximumRangePos = cameraPosition.add(cameraRotation.x * maxRange, cameraRotation.y * maxRange, cameraRotation.z * maxRange);
            final Box box = camera.getBoundingBox().stretch(cameraRotation.multiply(maxRange)).expand(1.0, 1.0, 1.0);

            final EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, cameraPosition, updatedMaximumRangePos, box, (entity) -> !entity.isSpectator() && entity.canHit(), squaredRange);

            return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(cameraPosition) < hitResultDistance ? ensureTargetInRange(entityHitResult, cameraPosition, entityInteractionRange) : ensureTargetInRange(hitResult, cameraPosition, blockInteractionRange);
        }

        private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
            final Vec3d hitResultPos = hitResult.getPos();

            if (!hitResultPos.isInRange(cameraPos, interactionRange)) {
                final Direction direction = Direction.getFacing(hitResultPos.x - cameraPos.x, hitResultPos.y - cameraPos.y, hitResultPos.z - cameraPos.z);
                return BlockHitResult.createMissed(hitResultPos, direction, BlockPos.ofFloored(hitResultPos));
            } else {
                return hitResult;
            }
        }

    }

}
