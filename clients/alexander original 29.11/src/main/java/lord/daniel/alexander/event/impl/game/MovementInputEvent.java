package lord.daniel.alexander.event.impl.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

/**
 * Written by Daniel. on 15/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
@Setter
@AllArgsConstructor
public class MovementInputEvent extends Event {
    private boolean forward, left, back, right;

}
