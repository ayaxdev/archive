package lord.daniel.alexander.util.prediction;

import net.minecraft.entity.Entity;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class PredictionUtil {

    public static double[] getPredicted(Entity entity) {
        final boolean sprinting = entity.isSprinting();
        final float walkingSpeed = 0.10000000149011612f;
        final float sprint = sprinting ? 1.25f : walkingSpeed;
        final float predictX = (float) ((entity.posX - entity.prevPosX) * sprint);
        final float predictZ = (float) ((entity.posZ - entity.prevPosZ) * sprint);
        return new double[] {entity.posX + predictX, entity.posY, entity.posZ + predictZ};
    }

}
