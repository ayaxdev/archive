package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class DirectInputEvent extends Event {
    public boolean left;
    public boolean right;
    public boolean backward;
    public boolean forward;
    public boolean sneak;
    public boolean jump;

    public DirectInputEvent(boolean left, boolean right, boolean backward, boolean forward, boolean sneak, boolean jump) {
        this.left = left;
        this.right = right;
        this.backward = backward;
        this.forward = forward;
        this.sneak = sneak;
        this.jump = jump;
    }
}
