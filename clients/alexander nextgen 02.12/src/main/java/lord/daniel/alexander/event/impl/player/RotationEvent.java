package lord.daniel.alexander.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

@AllArgsConstructor @Getter @Setter
public class RotationEvent extends Event {
    float yaw, pitch;
}