package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.client.toast.Toast;

public class ToastEvent extends Event {
    public Toast toast;

    public ToastEvent(Toast toast) {
        this.toast = toast;
    }
}
