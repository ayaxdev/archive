package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.JumpEvent;
import lord.daniel.alexander.event.impl.game.LivingUpdateEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.math.time.TimeHelper;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "HighJump", aliases = {"HigherJump"}, enumModuleType = EnumModuleType.MOVEMENT)
public class HighJumpModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Vanilla", new String[]{"Vanilla", "Vulcan", "VulcanFlag", "Verus", "AAC3", "DAC"});
    private final NumberValue<Float> motion = new NumberValue<>("Motion", this, 1F, 0.42F, 5f).addVisibleCondition(() -> mode.is("Vanilla"));
    private final NumberValue<Integer> verusCounter = new NumberValue<>("VerusMaximum", this, 10, 0, 50).addVisibleCondition(() -> mode.is("Verus"));

    private int verusJumpCounter = 0;

    private final TimeHelper vulcanTimer = new TimeHelper();
    private boolean vulcanJumped = false;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(mode.getValue());
    };

    @EventLink
    public final Listener<JumpEvent> jumpEventListener = jumpEvent -> {
        jumpEvent.setAllowJumpBoost(false);

        switch (mode.getValue()) {
            case "Vanilla" -> {
                jumpEvent.setUpwardsMotion(motion.getValue());
            }
            case "VulcanFlag" -> {
                jumpEvent.setUpwardsMotion(0.8995931202f);
            }
            case "Vulcan" -> {
                if(vulcanJumped) {
                    jumpEvent.setCancelled(true);
                }
                if(vulcanTimer.hasReached(2000)) {
                    jumpEvent.setUpwardsMotion(0.42f);
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 5, mc.thePlayer.posZ);
                    vulcanJumped = true;
                    vulcanTimer.reset();
                }
            }
            case "Verus" -> {
                verusJumpCounter = 0;
            }
        }
    };

    @EventLink
    public final Listener<LivingUpdateEvent> livingUpdateEventListener = livingUpdateEvent -> {
        switch (mode.getValue()) {
            case "AAC3" -> {
                if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.059;
            }
            case "DAC" -> {
                if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.049999;
            }
            case "Verus" -> {
                if(!mc.thePlayer.onGround && mc.thePlayer.motionY < 0.333 && verusJumpCounter <= verusCounter.getValue()) {
                    mc.thePlayer.motionY = 0.42f;
                    verusJumpCounter++;
                }
            }
            case "Vulcan" -> {
                if(mc.thePlayer.onGround) {
                   vulcanJumped = false;
                }

                if(vulcanJumped) {
                    //mc.thePlayer.onGround = true;
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
