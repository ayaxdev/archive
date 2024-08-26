package net.jezevcik.argon.event.impl;

public class RotationSetEvent {
    public final float yaw, pitch;
    public final boolean isYaw, isPitch;

    public RotationSetEvent(float yaw, float pitch, boolean isYaw, boolean isPitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.isYaw = isYaw;
        this.isPitch = isPitch;
    }
}
