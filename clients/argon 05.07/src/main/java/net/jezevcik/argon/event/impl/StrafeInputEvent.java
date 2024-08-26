package net.jezevcik.argon.event.impl;

public class StrafeInputEvent {

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
