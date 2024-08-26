package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.event.impl.AttackEntityEvent;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.util.player.PlayerUtil;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;
import net.minecraft.entity.EntityLivingBase;

public class MoreKBModule extends ModuleFeature {

    public MoreKBModule() {
        super(new ModuleData("MoreKB", "Allows you to give more knockback to entities", ModuleCategory.COMBAT)
                , null, null);
    }

    @Listen
    public final Listener<AttackEntityEvent> attackEntityEventListener = attackEntityEvent -> {
        if(attackEntityEvent.playerSp == mc.thePlayer && attackEntityEvent.attackedEntity instanceof EntityLivingBase) {
            PlayerUtil.sprintResetOnNextTick = true;
        }
    };

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
