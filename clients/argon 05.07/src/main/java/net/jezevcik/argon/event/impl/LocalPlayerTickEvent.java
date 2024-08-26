package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;

public class LocalPlayerTickEvent extends Cancellable {

    public final boolean pre;

    public LocalPlayerTickEvent(boolean pre) {
        this.pre = pre;
    }
}
