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
@Getter @Setter @AllArgsConstructor
public class HeadLookEvent extends Event {
    float yaw,pitch;
    double x, y, z;
}