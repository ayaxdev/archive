package com.skidding.atlas.processor.impl.storage;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.game.RunTickEvent;
import com.skidding.atlas.event.impl.client.TargetSetterEvent;
import com.skidding.atlas.processor.Processor;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class TargetStorage extends Processor {

    public static EntityLivingBase target;

    private final TimerUtil targetChangeTimer = new TimerUtil();

    @EventHandler(priority = 9999)
    public final void onTick(RunTickEvent runTickEvent) {
        if(runTickEvent.eventType == Event.EventType.POST) {
            EntityLivingBase nextTarget = null;

            if(mc.objectMouseOver != null) {
                Entity entity = mc.objectMouseOver.entityHit;

                if(entity instanceof EntityLivingBase entityLivingBase && !entity.isInvisible())
                    nextTarget = entityLivingBase;
            }

            TargetSetterEvent targetSetterEvent = new TargetSetterEvent(nextTarget);
            AtlasClient.getInstance().eventPubSub.publish(targetSetterEvent);
            nextTarget = targetSetterEvent.getNextTarget();

            if(nextTarget == null) {
                if(targetChangeTimer.hasElapsed(2000)) {
                    targetChangeTimer.reset();
                } else {
                    nextTarget = target;
                }
            }

            target = nextTarget;

            if(target == null)
                targetChangeTimer.reset();
        }
    }

}
