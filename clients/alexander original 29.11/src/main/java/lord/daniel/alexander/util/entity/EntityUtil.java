package lord.daniel.alexander.util.entity;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.util.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class EntityUtil implements Methods {

    public double getEffectiveHealth(Entity entity) {
        return ((EntityLivingBase)entity).getHealth() * (((EntityLivingBase)entity).getMaxHealth() / ((EntityLivingBase)entity).getTotalArmorValue());
    }

    public static double getRange(Entity entity) {
        if (mc.thePlayer == null)
            return 0;

        return mc.thePlayer.getPositionEyes(1.0f).distanceTo(RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1F),
                entity.getEntityBoundingBox()));
    }

}
