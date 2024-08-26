package lord.daniel.alexander.event.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@RequiredArgsConstructor
public class WorldLoadEvent extends Event {
    private final WorldClient worldClient;
}
