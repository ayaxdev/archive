package lord.daniel.alexander.handler.plaxer;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.Priorities;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.interfaces.Methods;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class PlayerHandler implements Methods {
    public static float yaw, pitch, prevYaw, prevPitch;
    public static boolean shouldSprintReset;

    public static int offTicks, onTicks;

    @EventLink(value = Priorities.VERY_HIGH)
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        if(updateMotionEvent.getStage() == UpdateMotionEvent.Stage.MID) {
            if(mc.thePlayer.onGround) {
                onTicks++;
                offTicks = 0;
            } else {
                offTicks++;
                onTicks = 0;
            }
        }
    };
}
