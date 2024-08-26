package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class StrafeInputEvent extends Event {

    public int moveForward;
    public int moveSideways;
    public boolean jumping;
    public boolean sneaking;

    public StrafeInputEvent(int moveForward, int moveSideways, boolean jumping, boolean sneaking) {
        this.moveForward = moveForward;
        this.moveSideways = moveSideways;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }
}
