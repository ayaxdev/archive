package com.skidding.atlas.event.impl.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;

@AllArgsConstructor
@Getter
public class TargetSetterEvent {

    private EntityLivingBase nextTarget;

    public void setNextTarget(EntityLivingBase nextTarget) {
        if(nextTarget != null)
            this.nextTarget = nextTarget;
    }
}
