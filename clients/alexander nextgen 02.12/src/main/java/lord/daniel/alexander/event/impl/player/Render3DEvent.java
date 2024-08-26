package lord.daniel.alexander.event.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;

@Getter
@RequiredArgsConstructor
public class Render3DEvent extends Event {
    private final float partialTicks;
}
