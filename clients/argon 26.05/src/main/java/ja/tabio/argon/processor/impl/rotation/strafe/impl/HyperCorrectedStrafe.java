package ja.tabio.argon.processor.impl.rotation.strafe.impl;

import ja.tabio.argon.event.impl.StrafeInputEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.processor.impl.rotation.strafe.StrafeCorrector;
import net.minecraft.util.math.MathHelper;

public class HyperCorrectedStrafe implements StrafeCorrector {

    private final double epsilon;
    private double partialAngles;

    public HyperCorrectedStrafe(double epsilon) {
        this.epsilon = epsilon;
    }

    public void reset() {
        partialAngles = 0;
    }

    @Override
    public void edit(float serverYaw, StrafeInputEvent event) {
        assert mc.player != null;

        if (Minecraft.inGame() && (event.moveForward != 0 || event.moveSideways != 0)) {
            final double angle = mc.player.getYaw() + Math.toDegrees(Math.atan2(-event.moveSideways, event.moveForward));

            event.moveForward = (int) Math.round(Math.cos(Math.toRadians(partialAngles + angle - serverYaw)));
            event.moveSideways = (int) Math.round(-Math.sin(Math.toRadians(partialAngles + angle - serverYaw)));

            final double serverAngle = serverYaw + Math.toDegrees(Math.atan2(-event.moveSideways, event.moveForward));

            partialAngles += MathHelper.wrapDegrees(angle - serverAngle) * epsilon;
        }
    }

}
