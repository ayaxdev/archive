package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class RotationSetEvent extends Event {
    public final float yaw, pitch;
    public final boolean isYaw, isPitch;

    public RotationSetEvent(float yaw, float pitch, boolean isYaw, boolean isPitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.isYaw = isYaw;
        this.isPitch = isPitch;
    }
}
