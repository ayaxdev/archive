package lord.daniel.alexander.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

@Getter @Setter @AllArgsConstructor
public class RendererEvent extends Event {
    float yaw, pitch;
}