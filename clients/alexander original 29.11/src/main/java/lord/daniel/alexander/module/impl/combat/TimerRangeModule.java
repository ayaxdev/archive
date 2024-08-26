package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.GameRendererEvent;
import lord.daniel.alexander.event.impl.game.PreTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Written by 'Stephen' for Alexander client.
 * Please don't use the code.
 *
 * @author Stephen.
 */
@ModuleData(name = "TimerRange", categories = {EnumModuleType.COMBAT, EnumModuleType.TICKBASE})
public class TimerRangeModule extends AbstractModule {
    private int counter = 0, rechargeTicks;
    private boolean freezing = false;
    private KillAuraModule killAuraModule;
    private final ExpandableValue rangeSettings = new ExpandableValue("Range", this);
    private final NumberValue<Float> maxRange = new NumberValue<>("MaxRange", this, 4F, 0F, 6F).addExpandableParents(rangeSettings);
    private final NumberValue<Float> minRange = new NumberValue<>("MinRange", this, 4F, 0F, 6F).addExpandableParents(rangeSettings);
    private final ExpandableValue timerSettings = new ExpandableValue("Timer", this);
    private final NumberValue<Float> highTimer = new NumberValue<>("HighTimer", this, 4F, 1F, 10F).addExpandableParents(timerSettings);
    private final NumberValue<Float> lowTimer = new NumberValue<>("LowTimer", this, 0.1F, 0.1F, 1F).addExpandableParents(timerSettings);
    private final BooleanValue reverse = new BooleanValue("Reverse", this, false).addExpandableParents(timerSettings);
    private final ExpandableValue tickSettings = new ExpandableValue("Tick", this);
    private final NumberValue<Integer> recharge = new NumberValue<>("RechargeTickDelay", this, 4, 0, 100).addExpandableParents(tickSettings);
    private final NumberValue<Integer> freezeTicks = new NumberValue<>("FreezeTicks", this, 4, 0, 20).addExpandableParents(tickSettings);
    private final NumberValue<Integer> teleportTicks = new NumberValue<>("TeleportTicks", this, 4, 0, 20).addExpandableParents(tickSettings);
    @EventLink
    public final Listener<PreTickEvent> preTickEventListener = preTickEvent -> {
        if (killAuraModule == null) {
            killAuraModule = ModuleStorage.getModuleStorage().getByClass(KillAuraModule.class);
        }

        EntityLivingBase target = getClosestPlayerWithin(7); // retarded but yes
        if (rechargeTicks > 0) {
            rechargeTicks--;
            return;
        }
        if (target == null || mc.thePlayer.ticksExisted <= 20) {
            rechargeTicks = recharge.getValue();
            freezing = false;
            counter = 0;
            return;
        }


        if (mc.thePlayer.getDistanceToEntity(target) > minRange.getValue() && mc.thePlayer.getDistanceToEntity(target) < maxRange.getValue().intValue()) {
            if (counter < (freezeTicks.getValue() - 1)) {
                freezing = true;
                preTickEvent.setCancelled(true);
            } else {
                mc.timer.elapsedTicks += teleportTicks.getValue();
                rechargeTicks = recharge.getValue();
                freezing = false;
                counter = 0;
            }
            counter++;
        }

    };
    @EventLink
    public final Listener<GameRendererEvent> gameRendererEventListener = preTickEvent -> {
        if (freezing) {
            if (counter < (freezeTicks.getValue() - 1)) {
                if (reverse.getValue()) {
                    mc.timer.timerSpeed = highTimer.getValue();
                } else {
                    mc.timer.timerSpeed = lowTimer.getValue();
                }
            } else {
                if (reverse.getValue()) {
                    mc.timer.timerSpeed = lowTimer.getValue();
                } else {
                    mc.timer.timerSpeed = highTimer.getValue();
                }
            }
        } else {
            mc.timer.timerSpeed = 1F;
        }
    };

    @Override
    public void onEnable() {
        counter = 0;
    }

    @Override
    public void onDisable() {

    }

    public static EntityPlayer getClosestPlayerWithin(double distance) {
        EntityPlayer target = null;
        if (mc.theWorld == null) {
            return null;
        }
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            float tempDistance = mc.thePlayer.getDistanceToEntity(entity);
            if (entity != mc.thePlayer && tempDistance <= distance) {
                target = entity;
                distance = tempDistance;
            }
        }
        return target;
    }
}
