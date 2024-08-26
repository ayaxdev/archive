package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

/**
 * Written by Daniel. on 15/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "NoFall", categories = EnumModuleType.PLAYER)
public class NoFallModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Vanilla", new String[]{"Vanilla", "Round"});
    private final BooleanValue onlyIfNeeded = new BooleanValue("OnlyIfNeeded", this, true).addVisibleCondition(mode, "Vanilla", "Round");

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        if(updateMotionEvent.getStage() == UpdateMotionEvent.Stage.MID) {
            switch (mode.getValueAsString()) {
                case "Vanilla" -> {
                    if(mc.thePlayer.fallDistance > 3.0D) {
                        updateMotionEvent.setOnGround(true);
                        if(onlyIfNeeded.getValue())
                            mc.thePlayer.fallDistance = 0;
                    }
                }
                case "Round" -> {
                    if(mc.thePlayer.fallDistance > 2.5D) {
                        updateMotionEvent.setPosY(Math.round(updateMotionEvent.getPosY()));
                        updateMotionEvent.setOnGround(true);
                        if(onlyIfNeeded.getValue())
                            mc.thePlayer.fallDistance = 0;
                    }
                }
                case "Block" -> {
                    if(mc.thePlayer.fallDistance > 3.0D) {
                        sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(updateMotionEvent.getPosX(), updateMotionEvent.getPosY(), updateMotionEvent.getPosZ(), PlayerHandler.yaw, PlayerHandler.pitch, true));
                        sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        mc.thePlayer.fallDistance = 0;
                    }
                }
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
