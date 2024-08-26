package com.atani.nextgen.processor.impl.storage;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.event.Event;
import com.atani.nextgen.event.impl.RunTickEvent;
import com.atani.nextgen.event.impl.SetTargetEvent;
import com.atani.nextgen.processor.Processor;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class TargetStorage extends Processor {

    public static EntityLivingBase target;

    @EventHandler(priority = 9999)
    public final void onTick(RunTickEvent runTickEvent) {
        if(runTickEvent.eventType == Event.EventType.POST) {
            EntityLivingBase nextTarget = target;

            if(mc.objectMouseOver != null) {
                Entity entity = mc.objectMouseOver.entityHit;

                if(entity instanceof EntityLivingBase entityLivingBase)
                    nextTarget = entityLivingBase;
            }

            SetTargetEvent setTargetEvent = new SetTargetEvent(nextTarget);
            AtaniClient.getInstance().eventPubSub.publish(setTargetEvent);
            nextTarget = setTargetEvent.getNextTarget();
        }
    }

}
