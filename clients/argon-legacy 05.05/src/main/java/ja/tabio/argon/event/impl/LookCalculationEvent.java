package ja.tabio.argon.event.impl;

public class LookCalculationEvent {
    public float rotationYaw, rotationPitch;

    public LookCalculationEvent(float rotationYaw, float rotationPitch) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
