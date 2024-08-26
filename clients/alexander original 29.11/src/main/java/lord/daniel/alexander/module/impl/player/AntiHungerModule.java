package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "AntiHunger", categories = {EnumModuleType.PLAYER, EnumModuleType.EXPLOIT})
public class AntiHungerModule extends AbstractModule {

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        final Packet<?> packet = packetEvent.getPacket();
        if (packet instanceof final C0BPacketEntityAction actionPacket) {
            final C0BPacketEntityAction.Action action = actionPacket.getAction();
            if(action == C0BPacketEntityAction.Action.STOP_SPRINTING || action == C0BPacketEntityAction.Action.START_SPRINTING) {
                packetEvent.setCancelled(true);
            }

        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
