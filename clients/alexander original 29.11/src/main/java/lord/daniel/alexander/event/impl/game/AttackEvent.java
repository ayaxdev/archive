package lord.daniel.alexander.event.impl.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lord.daniel.alexander.event.Event;
import net.minecraft.entity.Entity;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@AllArgsConstructor
@Getter
public class AttackEvent extends Event {
    private final Entity attacking;
}
