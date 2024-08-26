package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S40PacketDisconnect;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "AirStuck", categories = {EnumModuleType.PLAYER, EnumModuleType.EXPLOIT})
public class AirStuckModule extends AbstractModule {

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        final Packet<?> packet = packetEvent.getPacket();
        if(packet instanceof S40PacketDisconnect) {
            setEnabled(false);
        }
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        getPlayer().isDead = true;
        getPlayer().setVelocity(0, 0, 0);
        stopWalk();
        getPlayer().motionY = 0;
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        getPlayer().isDead = false;
        resumeWalk();
    }
}
