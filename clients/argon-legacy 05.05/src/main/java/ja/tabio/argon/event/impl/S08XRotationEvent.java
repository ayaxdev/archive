package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class S08XRotationEvent extends Event {
    public float rotationPitch;

    public S08XRotationEvent(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }
}
