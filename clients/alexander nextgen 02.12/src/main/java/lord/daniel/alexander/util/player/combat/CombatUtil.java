package lord.daniel.alexander.util.player.combat;

import lombok.Getter;
import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.player.rotation.RotationUtil;
import net.minecraft.entity.Entity;

public class CombatUtil implements IMinecraft {

    @Getter
    private static final CombatUtil combatUtil = new CombatUtil();

    public double getRange(Entity entity) {
        if (getPlayer() == null)
            return 0;

        return getPlayer().getPositionEyes(1.0f).distanceTo(RotationUtil.getRotationUtil().getBestVector(getPlayer().getPositionEyes(1F),
                entity.getEntityBoundingBox()));
    }

}
