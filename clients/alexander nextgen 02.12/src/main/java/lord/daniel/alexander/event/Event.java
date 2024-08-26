package lord.daniel.alexander.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
@Setter
public class Event {
    private boolean cancelled = false;
    private final Stage stage;

    public Event() {
        this(Stage.PRE);
    }

    public Event(final Stage stage) {
        this.stage = stage;
    }

    public enum Stage {
        PRE, MID, POST,
        RECEIVING, SENDING;
    }

}
