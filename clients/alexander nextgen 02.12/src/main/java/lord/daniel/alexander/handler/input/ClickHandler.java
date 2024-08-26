package lord.daniel.alexander.handler.input;

import io.github.nevalackin.radbus.Listen;
import lombok.Getter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.input.RegisterClickingEvent;
import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.math.time.MSTimer;

public class ClickHandler implements IMinecraft {

    private final MSTimer resetTimer = new MSTimer();
    private final MSTimer breakTimer = new MSTimer();

    @Getter
    private static int clicks;
    private static int lastAddedClick = 0;
    private int lastTick = 0;

    @Listen
    public final void onClicking(RegisterClickingEvent registerClickingEvent) {
        if(mc.thePlayer.ticksExisted <= 5)
            return;

        clicks = Math.min(20, clicks);
        if(registerClickingEvent.getStage() == Event.Stage.PRE) {
            if(resetTimer.hasReached(1000)) {
                clicks = 0;
                resetTimer.reset();
            }
        }
        if(registerClickingEvent.getStage() == Event.Stage.MID) {
            breakTimer.reset();
            while (clicks > 0) {
                if(lastTick == mc.thePlayer.ticksExisted)
                    break;

                // So the game doesn't get frozen for too long if an error happens
                if(breakTimer.hasReached(1000)) {
                    // TODO: implement a client logger for shit like this
                    System.out.println("WARNING had to manually unfreeze the game");
                    clicks = 0;
                    break;
                }
                //Modification.getModification().getPubSub().publish(new RegisterClickingEvent(Event.Stage.POST));
                if(!registerClickingEvent.isCancelled()) {
                    if(!registerClickingEvent.isCancelVanillaClick())
                        if(mc.clickMouse()) {
                            lastTick = mc.thePlayer.ticksExisted;
                            clicks--;
                        }
                    else {
                        lastTick = mc.thePlayer.ticksExisted;
                        clicks--;
                    }
                }
            }
        }
    }

    public static boolean incrementClick() {
        if(mc.thePlayer.ticksExisted != lastAddedClick) {
            clicks++;
            lastAddedClick = mc.thePlayer.ticksExisted;
            return true;
        }
        return false;
    }

}
