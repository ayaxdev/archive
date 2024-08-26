package ja.tabio.argon.processor.impl.rotation.interfaces;

import ja.tabio.argon.processor.impl.rotation.strafe.StrafeCorrector;

import java.util.TreeMap;

public interface Rotator {

    float[] rotate(float[] current, boolean tick, float tickDelta);

    boolean canRotate();

    int getPriorityRotations();

    TreeMap<Integer, RotationModifier> getModifiers();

    default StrafeCorrector getCorrector() {return null;}

    default int getFlags() {
        return 0;
    }

}
