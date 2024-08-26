package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.util.player.PlayerUtil;
import com.daniel.datsuzei.util.player.RotationUtil;

public class MovementCorrectionModule extends ModuleFeature {

    public MovementCorrectionModule() {
        super(new ModuleData("CorrectMovement", "Corrects your movement", ModuleCategory.MOVEMENT),
                null, null);
    }

    @Override
    protected void onEnable() {
        RotationUtil.movementAngleCorrection = true;
    }

    @Override
    protected void onDisable() {
        RotationUtil.movementAngleCorrection = false;
    }
}
