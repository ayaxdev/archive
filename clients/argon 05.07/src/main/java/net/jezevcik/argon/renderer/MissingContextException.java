package net.jezevcik.argon.renderer;

public class MissingContextException extends RuntimeException {

    public MissingContextException() {
        super("Attempted to call renderer method without providing context");
    }

}
