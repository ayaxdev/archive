package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class S08YRotationEvent extends Event {
    public float rotationYaw;

    public S08YRotationEvent(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }
}
