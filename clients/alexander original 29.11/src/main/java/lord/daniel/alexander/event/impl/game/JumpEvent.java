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
public class JumpEvent extends Event {
    private float upwardsMotion = 0f;
    private boolean allowJumpBoost = true;
}
