package ja.tabio.argon.utils.world;

import net.minecraft.entity.LivingEntity;

public class EntityUtils {

    public static double getEffectiveHealth(LivingEntity entity) {
        return entity.getHealth() * (entity.getMaxHealth() / entity.getArmor());
    }

}
