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
@AllArgsConstructor
@Getter
@Setter
public class UpdateMotionEvent extends Event {

    private final Stage stage;
    private double posX, posY, posZ;
    private boolean onGround;

    public enum Stage {
        PRE, MID, POST;
    }

}
