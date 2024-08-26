package net.jezevcik.argon.processor.impl.rotation.modifier;

import net.jezevcik.argon.processor.impl.rotation.interfaces.RotationModifier;
import net.jezevcik.argon.utils.math.MathUtils;
import net.jezevcik.argon.utils.player.MovementUtils;

public abstract class RelativeSmoothingModifier implements RotationModifier {

    @Override
    public float[] modifier(float[] from, float[] to, float[] server, float partialTicks, boolean back) {
        final float yawDifference = MovementUtils.Math.getAngleDifference(to[0], from[0]);
        final float pitchDifference = MovementUtils.Math.getAngleDifference(to[1], from[1]);

        final double rotationDifference = Math.hypot(Math.abs(yawDifference), Math.abs(pitchDifference));
        final double factor = computeFactor(rotationDifference, getTurnSpeed());

        final double straightLineYaw = Math.abs(yawDifference / rotationDifference) * factor;
        final double straightLinePitch = Math.abs(pitchDifference / rotationDifference) * factor;

        return new float[] {
                (float) (from[0] + MathUtils.clamp(yawDifference, -straightLineYaw, straightLineYaw)),
                (float) (from[1] + MathUtils.clamp(pitchDifference, -straightLinePitch, straightLinePitch))
        };
    }

    private double computeFactor(final double rotationDifference, final float turnSpeed) {
        return Math.min((rotationDifference / 120) * turnSpeed, 180f);
    }

    protected abstract float getTurnSpeed();

}
