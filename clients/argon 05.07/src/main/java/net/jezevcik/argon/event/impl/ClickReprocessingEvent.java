package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;
import net.jezevcik.argon.processor.impl.click.ClickCallback;

public class ClickReprocessingEvent extends Cancellable {

    public final ClickCallback clickCallback;

    public ClickReprocessingEvent(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }
}
