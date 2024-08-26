package lord.daniel.alexander.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;
import net.minecraft.entity.Entity;
@AllArgsConstructor @Getter @Setter
public class EntityRendererEvent extends Event {
    Entity entity;
}