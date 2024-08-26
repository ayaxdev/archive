package ja.tabio.argon.processor.impl.rotation.modifier;

import ja.tabio.argon.processor.impl.rotation.interfaces.RotationModifier;
import ja.tabio.argon.utils.math.MathUtils;
import ja.tabio.argon.utils.player.MovementUtils;

public abstract class LinearSmoothingModifier implements RotationModifier {

    @Override
    public float[] modifier(float[] from, float[] to, float[] server, float partialTicks, boolean back) {
        final float yawDifference = MovementUtils.Math.getAngleDifference(to[0], from[0]);
        final float pitchDifference = MovementUtils.Math.getAngleDifference(to[1], from[1]);

        final double rotationDifference = Math.hypot(Math.abs(yawDifference), Math.abs(pitchDifference));

        final float turnSpeed = getTurnSpeed();
        final double straightLineYaw = Math.abs(yawDifference / rotationDifference) * turnSpeed;
        final double straightLinePitch = Math.abs(pitchDifference / rotationDifference) * turnSpeed;

        return new float[] {
                (float) (from[0] + MathUtils.coerceIn(yawDifference, -straightLineYaw, straightLineYaw)),
                (float) (from[1] + MathUtils.coerceIn(pitchDifference, -straightLinePitch, straightLinePitch))
        };
    }

    protected abstract float getTurnSpeed();

}
