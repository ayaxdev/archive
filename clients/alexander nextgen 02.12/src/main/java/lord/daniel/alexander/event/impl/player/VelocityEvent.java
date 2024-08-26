package lord.daniel.alexander.event.impl.player;

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
@Getter
@Setter
public class VelocityEvent extends Event {
    private final Entity entity;
    private double motionX, motionY, motionZ;
    private boolean ignoreX, ignoreY, ignoreZ;

    public VelocityEvent(Entity entity, double motionX, double motionY, double motionZ) {
        this.entity = entity;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }
}
