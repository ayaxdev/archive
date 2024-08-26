package lord.daniel.alexander.event.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lord.daniel.alexander.event.Event;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Render2DEvent extends Event {
    private final ScaledResolution scaledResolution;
}
