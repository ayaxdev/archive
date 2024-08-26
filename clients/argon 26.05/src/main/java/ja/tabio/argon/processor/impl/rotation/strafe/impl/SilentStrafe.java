package ja.tabio.argon.processor.impl.rotation.strafe.impl;

import ja.tabio.argon.event.impl.StrafeInputEvent;
import ja.tabio.argon.processor.impl.rotation.strafe.StrafeCorrector;

public class SilentStrafe implements StrafeCorrector {

    @Override
    public void edit(float serverYaw, StrafeInputEvent event) {
        assert mc.player != null;

        if (event.moveForward != 0 || event.moveSideways != 0) {
            double angle = mc.player.getYaw() + Math.toDegrees(Math.atan2(-event.moveSideways, event.moveForward));
            event.moveForward = (int) Math.round(Math.cos(Math.toRadians(angle - serverYaw)));
            event.moveSideways = (int) Math.round(-Math.sin(Math.toRadians(angle - serverYaw)));
        }
    }

}
