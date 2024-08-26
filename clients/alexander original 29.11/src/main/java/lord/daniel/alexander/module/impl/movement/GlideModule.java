package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "Glide", enumModuleType = EnumModuleType.MOVEMENT)
public class GlideModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Vanilla", new String[]{"Vanilla", "Vulcan"});
    private final NumberValue<Double> motion = new NumberValue<>("Motion", this, 0.05D, 0.01D, 1D, 3).addVisibleCondition(() -> mode.is("Vanilla"));

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        if(updateMotionEvent.getStage() == UpdateMotionEvent.Stage.MID) {
            switch (mode.getValue()) {
                case "Vanilla" -> {
                    mc.thePlayer.motionY = -motion.getValue();
                }
                case "Vulcan" -> {
                    mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? -0.17 : -0.1;
                }
            }
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
