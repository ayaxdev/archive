package lord.daniel.alexander.event.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@RequiredArgsConstructor
public class KeyPressEvent extends Event {
    private final int key;
}
