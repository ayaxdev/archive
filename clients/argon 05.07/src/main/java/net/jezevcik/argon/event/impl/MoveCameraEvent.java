package net.jezevcik.argon.event.impl;

public class MoveCameraEvent {
    public final float tickDelta;

    public MoveCameraEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
