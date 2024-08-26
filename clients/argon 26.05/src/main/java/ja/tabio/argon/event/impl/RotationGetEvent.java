package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class RotationGetEvent extends Event {
    public float yaw, pitch;

    public RotationGetEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
