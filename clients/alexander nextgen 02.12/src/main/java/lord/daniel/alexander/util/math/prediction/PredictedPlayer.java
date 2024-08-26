package lord.daniel.alexander.util.math.prediction;

import lombok.Getter;
import lord.daniel.alexander.interfaces.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public record PredictedPlayer(Entity entity) implements IMinecraft {

    public Vec3 futurePosition(int ticks) {
        double x = entity.posX + ((entity.posX - entity.lastTickPosX) * ticks);
        double z = entity.posZ + ((entity.posZ - entity.lastTickPosZ) * ticks);
        return new Vec3(x, entity.posY, z);
    }

    private Vec3 localFuturePosition(int ticks) {
        double x = mc.thePlayer.posX + ((mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * ticks);
        double z = mc.thePlayer.posZ + ((mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * ticks);
        return new Vec3(x, mc.thePlayer.posY, z);
    }

    public double distanceToLocal(int ticks) {
        double distanceFuture = futurePosition(ticks).distanceTo(new Vec3(mc.thePlayer.posX + (mc.thePlayer.motionX * ticks), mc.thePlayer.posY, mc.thePlayer.posZ + (mc.thePlayer.motionZ * ticks)));
        double distanceNormal = localFuturePosition(ticks).distanceTo(entity.getPositionVector());

        return Math.min(distanceFuture, distanceNormal);
    }

    public Vec3 differenceBetweenFuture(int ticks) {
        return futurePosition(ticks).subtract(new Vec3(entity.posX, entity.posY, entity.posZ));
    }

}