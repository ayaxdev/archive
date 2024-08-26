package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class ReloadingLoadingOverlayEvent extends Event {
    public boolean reloading;

    public ReloadingLoadingOverlayEvent(boolean reloading) {
        this.reloading = reloading;
    }
}
