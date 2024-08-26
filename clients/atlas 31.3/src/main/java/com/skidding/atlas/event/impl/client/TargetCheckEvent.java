package com.skidding.atlas.event.impl.client;

import com.skidding.atlas.event.Event;
import net.minecraft.entity.EntityLivingBase;

public class TargetCheckEvent extends Event {
    public EntityLivingBase entityLivingBase;
    public boolean allow = true;

    public TargetCheckEvent(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }
}
