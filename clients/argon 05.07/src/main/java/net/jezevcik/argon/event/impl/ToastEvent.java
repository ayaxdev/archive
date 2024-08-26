package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;
import net.minecraft.client.toast.Toast;

public class ToastEvent extends Cancellable {
    public final Toast toast;

    public ToastEvent(Toast toast) {
        this.toast = toast;
    }
}
