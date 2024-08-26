package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;

public class MovementEvent extends Cancellable {

    public double velocityX, velocityY, velocityZ;

    public MovementEvent(double velocityX, double velocityY, double velocityZ) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }
}
