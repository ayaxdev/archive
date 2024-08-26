package lord.daniel.alexander.event.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lord.daniel.alexander.event.Event;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Written by Daniel. on 23/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
public class Render2DEvent extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Render2DEvent(Stage stage, ScaledResolution scaledResolution, float partialTicks) {
        super(stage);
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }
}
