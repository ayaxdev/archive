package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.radbus.Listen;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.player.UpdateEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.player.MoveUtil;

@CreateModule(name = "Flight", category = EnumModuleType.MOVEMENT)
public class FlightModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Motion", new String[]{"Motion", "Creative"});
    private final NumberValue<Float> speed = new NumberValue<Float>("Speed", this, 2f, 0f, 7f, 1).addVisibleCondition(mode, "Motion");
    private final BooleanValue stopOnDisable = new BooleanValue("StopOnDisable", this, true);
    private final BooleanValue stopYMotion = new BooleanValue("StopYMotion", this, false).addVisibleCondition(stopOnDisable);
    private final BooleanValue stopOnlyPositiveYMotion = new BooleanValue("StopOnlyPositiveYMotion", this, true).addVisibleCondition(stopYMotion).addVisibleCondition(stopOnDisable);

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        final MoveUtil moveUtil = MoveUtil.getMoveUtil();

        if(updateEvent.getStage() == Event.Stage.MID) {
            switch (mode.getValue()) {
                case "Motion" -> {
                    if(moveUtil.isMoving()) {
                        moveUtil.setSpeed(speed.getValue());
                    } else {
                        moveUtil.setSpeed(0);
                    }
                    if(isKeyDown(getGameSettings().keyBindJump)) {
                        getPlayer().motionY = speed.getValue();
                    } else if(isKeyDown(getGameSettings().keyBindSneak)) {
                        getPlayer().motionY = -speed.getValue();
                    } else {
                        getPlayer().motionY = 0;
                    }
                }
                case "Creative" -> mc.thePlayer.capabilities.isFlying = true;
            }
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;
        if(stopOnDisable.getValue()) {
            MoveUtil.getMoveUtil().setSpeed(0);

            if(stopYMotion.getValue()) {
                if(!stopOnlyPositiveYMotion.getValue() || mc.thePlayer.motionY > 0) {
                    mc.thePlayer.motionY = 0;
                }
            }
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}
