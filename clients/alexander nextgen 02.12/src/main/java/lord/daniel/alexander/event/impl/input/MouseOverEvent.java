package lord.daniel.alexander.event.impl.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;
import net.minecraft.entity.Entity;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter @Setter @AllArgsConstructor
public class MouseOverEvent extends Event {
    private double range;
    private boolean rangeCheck;
    private final Entity entity;
}