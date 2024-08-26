package lord.daniel.alexander.event.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@RequiredArgsConstructor
public class PacketEvent extends Event {
    private final Packet<?> packet;
    private final INetHandler iNetHandler;
    private final Stage stage;
    private final EnumPacketDirection direction;

    public enum Stage {
        SENDING, RECEIVING;
    }
}
