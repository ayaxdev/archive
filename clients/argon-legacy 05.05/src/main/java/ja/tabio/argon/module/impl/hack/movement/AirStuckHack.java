package ja.tabio.argon.module.impl.hack.movement;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.PlayerUpdateEvent;
import ja.tabio.argon.event.impl.ProcessPacketEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.setting.impl.BooleanSetting;
import net.minecraft.network.play.server.S40PacketDisconnect;
import org.lwjgl.input.Keyboard;

@ModuleData(name = "AirStuck", category = ModuleCategory.HACK)
@HackData(hackCategory = HackCategory.MOVEMENT)
public class AirStuckHack extends Module {

    public final BooleanSetting dead = new BooleanSetting("Dead", true);

    @EventHandler
    public final void onPacket(ProcessPacketEvent packetEvent) {
        if (packetEvent.packet instanceof S40PacketDisconnect)
            setEnabled(false);
    }

    @EventHandler
    public final void onUpdate(PlayerUpdateEvent playerUpdateEvent) {
        if (dead.getValue())
            mc.thePlayer.isDead = true;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null)
            mc.thePlayer.isDead = false;
        mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
    }

}
