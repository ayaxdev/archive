package ja.tabio.argon.event.impl;

import java.util.LinkedList;
import java.util.List;

public class PlayerRotationEvent {
    private float rotationYaw, rotationPitch;

    public boolean legit = true;
    public List<RotationModifier> modifiers = new LinkedList<>();

    public PlayerRotationEvent(float rotationYaw, float rotationPitch) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
        this.legit = false;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
        this.legit = false;
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public static abstract class RotationModifier {
        public abstract float[] run(float[] currentAngle, float[] nextAngle);
    }

}
