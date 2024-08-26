package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.ClickingEvent;
import lord.daniel.alexander.event.impl.game.OnTickEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.RandomizedNumberValue;
import lord.daniel.alexander.util.math.time.TimeHelper;
import net.minecraft.item.ItemBlock;
import org.lwjgl.input.Mouse;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "RightClicker", categories = {EnumModuleType.WORLD, EnumModuleType.PLAYER, EnumModuleType.GHOST, EnumModuleType.INPUT})
public class RightClickerModule extends AbstractModule {

    private final NumberValue<Integer> cps = new NumberValue<>("CPS", this, 10, 1, 20, 0);
    private final StringModeValue cpsMode = new StringModeValue("CPSMode", this, "Randomize", new String[]{"Randomize", "Static"});
    private final RandomizedNumberValue<Float> cpsDeviation = new RandomizedNumberValue<>("CPSDeviation", this, -2f, 2f, -10f, 10f, 1).addVisibleCondition(() -> cpsMode.is("Randomize"));
    private final BooleanValue reduceInAir = new BooleanValue("ReduceInAir", this, true);
    private final RandomizedNumberValue<Float> airReduceCps = new RandomizedNumberValue<>("AirCPSReduce", this, 1f, 2f, 0f, 20f, 1).addVisibleCondition(reduceInAir::getValue);
    private final NumberValue<Float> cpsLimit = new NumberValue<>("CPSLimit", this, 20f, 0f, 50f, 1);
    private final BooleanValue resetCPS = new BooleanValue("ResetCPS", this, true);
    private final BooleanValue rightClickCounter = new BooleanValue("CounterDelay", this, false);
    private final MultiSelectValue clickEvent = new MultiSelectValue("ClickEvent", this, new String[]{"OnClicking", "OnTick"}, new String[]{"OnClicking", "OnTick", "OnPreUpdate", "OnPostUpdate", "OnPreMotion", "OnPostMotion"});
    private final BooleanValue blocksOnly = new BooleanValue("BlocksOnly", this, true);

    private long calcCps = 0L;
    private final TimeHelper clickTimer = new TimeHelper();

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(cps.getValue() + "CPS");
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        switch (updateMotionEvent.getStage()) {
            case PRE -> {
                if(clickEvent.is("OnPreUpdate"))
                    onAttack();
            }
            case MID -> {
                if(clickEvent.is("OnPostUpdate") || clickEvent.is("OnPreMotion"))
                    onAttack();
            }
            case POST -> {
                if(clickEvent.is("OnPostMotion"))
                    onAttack();
            }
        }
    };

    @EventLink
    public final Listener<OnTickEvent> onTickEventListener = event -> {
        if(clickEvent.is("OnTick"))
            onAttack();
    };

    @EventLink
    public final Listener<ClickingEvent> clickingEventListener = e -> {
        if(clickEvent.is("OnClicking"))
            onAttack();
    };

    public void onAttack() {
        if(shouldBlockClick()) {
            if(resetCPS.getValue())
                calcCps = 0L;
        } else {
            if(clickTimer.hasReached(calcCps) && !(this.rightClickCounter.getValue() && mc.getRightClickDelayTimer() != 0)) {
                mc.rightClickMouse();
                clickTimer.reset();

                calcCps = calculateCPS();
            }
        }
    }

    public long calculateCPS() {
        float cps = this.cps.getValue();

        if(this.reduceInAir.getValue() && !mc.thePlayer.onGround) {
            cps -= airReduceCps.getValue();
        }

        if (cpsMode.getValue().equals("Randomize")) {
            cps += cpsDeviation.getValue();
        }
        return (long) (1000 / Math.min(cps, this.cpsLimit.getValue()));
    }

    private boolean shouldBlockClick() {
        if(mc.currentScreen != null)
            return true;

        if(blocksOnly.getValue() && mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))
            return true;

        return !Mouse.isButtonDown(1);
    }

    @Override
    public void onEnable() {
        
    }

    @Override
    public void onDisable() {

    }
}
