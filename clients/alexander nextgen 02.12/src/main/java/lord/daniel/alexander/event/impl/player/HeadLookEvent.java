package lord.daniel.alexander.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

@Getter @Setter @AllArgsConstructor
public class HeadLookEvent extends Event {
    float yaw,pitch;
    double x, y, z;
}