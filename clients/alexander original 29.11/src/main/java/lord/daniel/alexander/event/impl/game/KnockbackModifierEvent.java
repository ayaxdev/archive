package lord.daniel.alexander.event.impl.game;

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
public class KnockbackModifierEvent extends Event {
    private Entity entity;
    private boolean flag;
}