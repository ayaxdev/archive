package lord.daniel.alexander.event;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Setter
@Getter
public class Event {

    private boolean cancelled;

    public <T extends Event> T publishItself() {
        Modification.INSTANCE.getBus().post(this);
        return (T) this;
    }

}
