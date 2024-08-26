package lord.daniel.alexander.event.impl.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@Setter
public class TimeEvent extends Event {
    private long time;
    private boolean freeze;
    private long freezeTime;

    public TimeEvent(long time) {
        this.time = time;
    }
}
