package ja.tabio.argon.processor.impl.rotation.strafe.impl;

import ja.tabio.argon.event.impl.StrafeInputEvent;
import ja.tabio.argon.processor.impl.rotation.strafe.StrafeCorrector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BreezilyStrafe implements StrafeCorrector {


    @Override
    public void edit(float serverYaw, StrafeInputEvent event) {
        if (event.moveSideways == 0 && event.moveForward == 0)
            return;

        assert mc.player != null;

        final double angle = mc.player.getYaw() + Math.toDegrees(Math.atan2(-event.moveSideways, event.moveForward));

        // cos(a): a=0 -> 1, a=45 -> 0.5, a=90 -> 0
        // cos(0) = 1; 1² = 1
        // cos(45) = 0.707...; 0.707² = 0.5
        // cos(90) = 0; 0² = 0
        final double impactX = Math.cos(Math.toRadians(angle)) * Math.cos(Math.toRadians(angle));
        final double impactZ = Math.sin(Math.toRadians(angle)) * Math.sin(Math.toRadians(angle));

        final Vec3d next = mc.player.getPos().add(-Math.sin(Math.toRadians(angle)) * 0.5, 0, Math.cos(Math.toRadians(angle)) * 0.5);
        final Vec3d center = Vec3d.ofCenter(BlockPos.ofFloored(next));
        final Vec3d goTo = new Vec3d(
                MathHelper.lerp(impactX, next.x, center.x),
                0,
                MathHelper.lerp(impactZ, next.z, center.z)
        );

        final double sollAngle = Math.toDegrees(Math.atan2(mc.player.getX() - goTo.x, goTo.z - mc.player.getZ()));
        event.moveForward = (int) Math.round(Math.cos(Math.toRadians(sollAngle - serverYaw)));
        event.moveSideways = (int) Math.round(-Math.sin(Math.toRadians(sollAngle - serverYaw)));
    }

}
