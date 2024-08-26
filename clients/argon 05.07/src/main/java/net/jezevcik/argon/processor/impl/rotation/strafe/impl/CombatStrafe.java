package net.jezevcik.argon.processor.impl.rotation.strafe.impl;

import net.jezevcik.argon.event.impl.StrafeInputEvent;
import net.jezevcik.argon.processor.impl.rotation.strafe.StrafeCorrector;

public class CombatStrafe implements StrafeCorrector {

    @Override
    public void edit(float serverYaw, StrafeInputEvent event) {
        assert client.player != null;

        if (event.moveForward != 0 || event.moveSideways != 0) {
            final double angle = client.player.getYaw() + Math.toDegrees(Math.atan2(-event.moveSideways, event.moveForward));

            if (event.moveForward > 0 && !client.player.horizontalCollision && client.options.sprintKey.isPressed()) {
                event.moveForward = 1;
            } else {
                event.moveForward = (int) Math.round(Math.cos(Math.toRadians(angle - serverYaw)));
            }

            event.moveSideways = (int) Math.round(-Math.sin(Math.toRadians(angle - serverYaw)));
        }
    }

}
