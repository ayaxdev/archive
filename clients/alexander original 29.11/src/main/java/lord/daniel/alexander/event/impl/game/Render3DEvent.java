package lord.daniel.alexander.event.impl.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lord.daniel.alexander.event.Event;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@AllArgsConstructor
@Getter
public class Render3DEvent extends Event {

    private final float partialTicks;

}
