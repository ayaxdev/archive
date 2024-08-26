package net.jezevcik.argon.event;

public class Cancellable {

    public boolean cancelled;

    public void cancel() {
        cancelled = true;
    }

}
