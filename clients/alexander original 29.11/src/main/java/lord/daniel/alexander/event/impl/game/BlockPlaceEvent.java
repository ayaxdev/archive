package lord.daniel.alexander.event.impl.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@Setter
@AllArgsConstructor
public class BlockPlaceEvent extends Event {
    private final BlockPos blockPos;
    private final EnumFacing side;
    private final Vec3 hitVec;
}