package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.*;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.RandomizedNumberValue;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;
import lord.daniel.alexander.util.math.random.impl.SecureRandomAlgorithm;
import lord.daniel.alexander.util.math.time.TimeHelper;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "LeftClicker", categories = {EnumModuleType.COMBAT, EnumModuleType.PLAYER, EnumModuleType.GHOST, EnumModuleType.INPUT})
public class LeftClickerModule extends AbstractModule {

    private final ExpandableValue cpsSettings = new ExpandableValue("CPS", this);
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", this, 10, 1, 20, 0).addExpandableParents(cpsSettings);
    private final NumberValue<Float> cpsLimit = new NumberValue<>("CPSLimit", this, 20f, 0f, 50f, 1).addExpandableParents(cpsSettings);
    private final StringModeValue cpsMode = new StringModeValue("CPSMode", this, "Randomize", new String[]{"Randomize", "Static"}).addExpandableParents(cpsSettings);
    private final RandomizedNumberValue<Float> cpsDeviation = new RandomizedNumberValue<>("CPSDeviation", this, -2f, 2f, -10f, 10f, 1).addVisibleCondition(() -> cpsMode.is("Randomize")).addExpandableParents(cpsSettings);
    private final BooleanValue reduceInAir = new BooleanValue("ReduceInAir", this, true);
    private final RandomizedNumberValue<Float> airReduceCps = new RandomizedNumberValue<>("AirCPSReduce", this, 1f, 2f, 0f, 20f, 1).addVisibleCondition(reduceInAir::getValue).addExpandableParents(cpsSettings);
    private final BooleanValue resetCPS = new BooleanValue("ResetCPS", this, true);
    private final BooleanValue leftClickCounter = new BooleanValue("1.8CounterDelay", this, false);
    private final MultiSelectValue clickEvent = new MultiSelectValue("ClickEvent", this, new String[]{"OnClicking", "OnTick"}, new String[]{"OnClicking", "OnTick", "OnPreUpdate", "OnPostUpdate", "OnPreMotion", "OnPostMotion"});
    private final BooleanValue weaponsOnly = new BooleanValue("WeaponsOnly", this, true);
    private final BooleanValue breakingBlocks = new BooleanValue("AllowBreakingBlocks", this, true);
    private final BooleanValue blockWithPearls = new BooleanValue("BlockWithPearls", this, true);

    private RandomizationAlgorithm randomizationAlgorithm = new SecureRandomAlgorithm();
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
            if(clickTimer.hasReached(calcCps) && !(this.leftClickCounter.getValue() && mc.leftClickCounter != 0)) {
                mc.clickMouse();
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

        if(weaponsOnly.getValue() && mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemAxe))
            return true;

        if(blockWithPearls.getValue() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl)
            return true;

        if(breakingBlocks.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            return true;

        return !Mouse.isButtonDown(0);
    }

    @Override
    public void onEnable() {
        
    }

    @Override
    public void onDisable() {

    }
}
