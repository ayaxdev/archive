package net.jezevcik.argon.processor.impl.rotation.modifier;

import net.jezevcik.argon.processor.impl.rotation.interfaces.RotationModifier;
import net.jezevcik.argon.utils.math.MathUtils;
import net.jezevcik.argon.utils.player.MovementUtils;
import net.minecraft.util.hit.HitResult;

public abstract class ConditionalSmoothingModifier implements RotationModifier {

    @Override
    public float[] modifier(float[] from, float[] to, float[] server, float partialTicks, boolean back) {
        final boolean crosshair = client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY;
        final double distance = client.crosshairTarget == null ? 0 : client.crosshairTarget.getPos().distanceTo(client.player.getPos());

        final float yawDifference = MovementUtils.Math.getAngleDifference(to[0], from[0]);
        final float pitchDifference = MovementUtils.Math.getAngleDifference(to[1], from[1]);

        final double rotationDifference = Math.hypot(Math.abs(yawDifference), Math.abs(pitchDifference));
        final double[] factor = computeTurnSpeed(distance, Math.abs(yawDifference), Math.abs(pitchDifference), crosshair);

        final double straightLineYaw = Math.max(Math.abs(yawDifference / rotationDifference) * factor[0], getMinimumTurnSpeed()[0]);
        final double straightLinePitch = Math.max(Math.abs(pitchDifference / rotationDifference) * factor[1], getMinimumTurnSpeed()[1]);

        return new float[] {
                (float) (from[0] + MathUtils.clamp(yawDifference, -straightLineYaw, straightLineYaw)),
                (float) (from[1] + MathUtils.clamp(pitchDifference, -straightLinePitch, straightLinePitch))
        };
    }

    public double[] computeTurnSpeed(double distance, float diffH, float diffV, boolean crosshair) {
        double turnSpeedH = getCoefDistance() * distance + getCoefDiff()[0] * diffH +
                (crosshair ? getCoefCrosshair()[0] : 0f) + getIntercept()[0];
        double turnSpeedV = getCoefDistance() * distance + getCoefDiff()[1] * Math.max(0f, diffV - diffH) +
                (crosshair ? getCoefCrosshair()[1] : 0f) + getIntercept()[1];
        return new double[] {Math.max(Math.abs(turnSpeedH), getMinimumTurnSpeed()[0]), Math.max(Math.abs(turnSpeedV), getMinimumTurnSpeed()[1])};
    }

    protected abstract float getCoefDistance();

    protected abstract float[] getCoefDiff();

    protected abstract float[] getCoefCrosshair();

    protected abstract float[] getIntercept();


    protected abstract float[] getMinimumTurnSpeed();

}
