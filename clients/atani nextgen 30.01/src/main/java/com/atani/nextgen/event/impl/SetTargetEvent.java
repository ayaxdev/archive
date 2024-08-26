package com.atani.nextgen.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

@AllArgsConstructor
@Getter
public class SetTargetEvent {

    private EntityLivingBase nextTarget;

    public void setNextTarget(EntityLivingBase nextTarget) {
        if(nextTarget != null)
            this.nextTarget = nextTarget;
    }
}
