package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.LivingUpdateEvent;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "AirJump", enumModuleType = EnumModuleType.MOVEMENT)
public class AirJumpModule extends AbstractModule {

    private final BooleanValue spoofGround = new BooleanValue("SpoofGround", this, false);

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(spoofGround.getValue() ? "Spoof" : "Normal");
    };

    @EventLink
    public final Listener<LivingUpdateEvent> livingUpdateEventListener = livingUpdateEvent -> {
        if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
            mc.thePlayer.onGround = true;
        }
    };

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if(packetEvent.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packetEvent.getPacket();
            if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && spoofGround.getValue()) {
                c03PacketPlayer.setOnGround(true);
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
