package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;

public class ServerPlayerTickEvent extends Cancellable {

    public final boolean pre;

    public ServerPlayerTickEvent(boolean pre) {
        this.pre = pre;
    }
}
