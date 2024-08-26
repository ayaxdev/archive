package lord.daniel.alexander.event.impl.game;

import lombok.Getter;
import lord.daniel.alexander.event.Event;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@Getter
public class PacketEvent extends Event {

    private final Packet<?> packet;
    private final INetHandler netHandler;

    public PacketEvent(Stage stage, Packet<?> packet, INetHandler netHandler) {
        super(stage);
        this.packet = packet;
        this.netHandler = netHandler;
    }
}
