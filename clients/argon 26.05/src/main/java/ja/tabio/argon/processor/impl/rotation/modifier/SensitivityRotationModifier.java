package ja.tabio.argon.processor.impl.rotation.modifier;

import ja.tabio.argon.Argon;
import ja.tabio.argon.processor.impl.rotation.interfaces.RotationModifier;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

public abstract class SensitivityRotationModifier implements RotationModifier {

    private static double partialIterations;

    @Override
    public float[] modifier(float[] from, float[] to, float[] server, float partialTicks, boolean back) {
        if (back)
            return to;

        final double sensitivity = mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
        final double sensitivityCb = sensitivity * sensitivity * sensitivity;
        final double gcd = sensitivityCb * 8.0;

        final float deltaYaw = MathHelper.subtractAngles(from[0], to[0]);
        final float deltaPitch = MathHelper.subtractAngles(from[1], to[1]);

        final SensitivityMode sensitivityMode = getSensitivityMode();
        final float[] returnRotations = new float[] {server[0], server[1]};

        switch (getSensitivityMode()) {
            case TICK_BASED -> {
                returnRotations[0] = from[0];
                returnRotations[1] = from[1];
                float gcdCursorDeltaX = deltaYaw / 0.15F;
                float gcdCursorDeltaY = deltaPitch / 0.15F;

                double cursorDeltaX = gcdCursorDeltaX / gcd;
                double cursorDeltaY = gcdCursorDeltaY / gcd;

                final int roundedCursorDeltaX = (int) Math.round(cursorDeltaX);
                final int roundedCursorDeltaY = (int) Math.round(cursorDeltaY);

                cursorDeltaX = roundedCursorDeltaX * gcd;
                cursorDeltaY = roundedCursorDeltaY * gcd;

                gcdCursorDeltaX = (float) cursorDeltaX * 0.15F;
                gcdCursorDeltaY = (float) cursorDeltaY * 0.15F;

                returnRotations[0] += gcdCursorDeltaX;
                returnRotations[1] += gcdCursorDeltaY;
            }
            case APPROXIMATE, REAL -> {
                double iterationsNeeded = (sensitivityMode == SensitivityMode.APPROXIMATE ?
                        ThreadLocalRandom.current().nextDouble(20, 60) : Argon.getInstance().renderManager.getFps())
                        / 20.0;
                iterationsNeeded *= partialTicks;
                final int iterations = MathHelper.floor(iterationsNeeded + partialIterations);
                partialIterations += iterationsNeeded - iterations;

                returnRotations[0] = from[0];
                returnRotations[1] = from[1];

                final float gcdCursorDeltaX = deltaYaw / 0.15F;
                final float gcdCursorDeltaY = deltaPitch / 0.15F;

                final double cursorDeltaX = gcdCursorDeltaX / gcd;
                final double cursorDeltaY = gcdCursorDeltaY / gcd;

                double partialDeltaX = 0;
                double partialDeltaY = 0;

                for (int i = 0; i < iterations; i++) {
                    double sollDeltaX = cursorDeltaX / iterations;
                    double sollDeltaY = cursorDeltaY / iterations;

                    int istDeltaX = (int) Math.round(sollDeltaX + partialDeltaX);
                    int istDeltaY = (int) Math.round(sollDeltaY + partialDeltaY);

                    partialDeltaX += sollDeltaX - istDeltaX;
                    partialDeltaY += sollDeltaY - istDeltaY;

                    final double newCursorDeltaX = istDeltaX * gcd;
                    final double newCursorDeltaY = istDeltaY * gcd;

                    returnRotations[0] += (float) newCursorDeltaX * 0.15F;
                    returnRotations[1] += (float) newCursorDeltaY * 0.15F;
                }
            }
        }

        return returnRotations;
    }

    protected abstract SensitivityMode getSensitivityMode();

    public enum SensitivityMode {
        TICK_BASED("TickBased", "Simulates 20FPS"),
        APPROXIMATE("Approximate", "Simulates 20-60FPS"),
        REAL("Real", "Simulates your current FPS");

        final String name, detail;

        SensitivityMode(String name, String detail) {
            this.name = name;
            this.detail = detail;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
