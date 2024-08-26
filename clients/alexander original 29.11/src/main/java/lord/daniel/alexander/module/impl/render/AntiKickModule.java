package lord.daniel.alexander.module.impl.render;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import net.minecraft.network.play.server.S40PacketDisconnect;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "AntiKick", enumModuleType = EnumModuleType.RENDER)
public class AntiKickModule extends AbstractModule {

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if(packetEvent.getStage() == PacketEvent.Stage.RECEIVING && packetEvent.getPacket() instanceof S40PacketDisconnect) {
            packetEvent.setCancelled(true);
            sendMessage("It appears, though, that you have been kicked. Reason: " + ((S40PacketDisconnect) packetEvent.getPacket()).getReason().getFormattedText());
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
