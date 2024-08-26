package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class LoadingOverlayEvent extends Event {
    public final long reloadCompleteTime;

    public LoadingOverlayEvent(long reloadCompleteTime) {
        this.reloadCompleteTime = reloadCompleteTime;
    }
}
