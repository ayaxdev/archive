package lord.daniel.alexander.event.impl.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@RequiredArgsConstructor
@Getter
public class KeyInputEvent extends Event {
    private final int key;
}
