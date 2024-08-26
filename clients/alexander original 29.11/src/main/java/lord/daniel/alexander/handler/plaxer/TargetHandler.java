package lord.daniel.alexander.handler.plaxer;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lord.daniel.alexander.event.impl.game.AttackEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.impl.combat.KillAuraModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.math.time.TimeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Written by Daniel. on 18/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class TargetHandler {

    private final TimeHelper attackTimer = new TimeHelper();
    private static KillAuraModule killAuraModule;

    private static Entity entity;
    private static Entity attackEntity;

    @EventLink
    public final Listener<AttackEvent> attackEventListener = attackEvent -> {
        if(attackEvent.getAttacking() instanceof EntityLivingBase) {
            entity = attackEvent.getAttacking();
            attackTimer.reset();
        }
    };

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        if(attackTimer.hasReached(1500))
            attackEntity = null;

        if(killAuraModule == null)
            killAuraModule = ModuleStorage.getModuleStorage().getByClass(KillAuraModule.class);

        if(killAuraModule.target == null)
            entity = attackEntity;
        else
            entity = killAuraModule.target;
    };

    public static Entity getEntity(boolean onlyKillAura) {
        if(onlyKillAura && entity != killAuraModule.target)
            return null;
        return entity;
    }

}
