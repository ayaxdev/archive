package lord.daniel.alexander.event.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;
import net.minecraft.entity.EntityLivingBase;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@RequiredArgsConstructor
@Getter
public class SwingEvent extends Event {
    private final EntityLivingBase entity;

}
