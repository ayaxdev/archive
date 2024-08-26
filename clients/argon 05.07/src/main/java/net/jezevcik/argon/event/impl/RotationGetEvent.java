package net.jezevcik.argon.event.impl;

public class RotationGetEvent {
    public float yaw, pitch;

    public RotationGetEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
