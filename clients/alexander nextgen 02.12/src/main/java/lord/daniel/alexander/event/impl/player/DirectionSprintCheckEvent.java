package lord.daniel.alexander.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;

@AllArgsConstructor
@Setter
@Getter
public class DirectionSprintCheckEvent extends Event {
    private boolean sprintCheck;
}
