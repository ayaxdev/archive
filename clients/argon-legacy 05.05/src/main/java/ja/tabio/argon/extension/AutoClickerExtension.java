package ja.tabio.argon.extension;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.BackgroundEvent;
import ja.tabio.argon.event.impl.PlayerUpdateEvent;
import ja.tabio.argon.event.impl.PreAttackEvent;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.utils.math.time.TimerUtil;

import java.util.function.Supplier;

public class AutoClickerExtension implements IMinecraft {

    private final Supplier<ClickEvent> clickEvent;
    private final Supplier<Boolean> isEnabled;
    private final Supplier<Float> cps;
    private final Supplier<Boolean> onClick;

    public AutoClickerExtension(Supplier<Boolean> isEnabled, Supplier<Float> cps, Supplier<Boolean> onClick, Supplier<ClickEvent> clickEvent) {
        this.isEnabled = isEnabled;
        this.cps = cps;
        this.onClick = onClick;
        this.clickEvent = clickEvent;
    }

    public AutoClickerExtension(Supplier<Boolean> isEnabled, Supplier<Float> cps, Supplier<ClickEvent> clickEvent) {
        this(isEnabled, cps, () -> {
            mc.clickMouse();
            return true;
        }, clickEvent);
    }

    @EventHandler
    public final void onUpdate(PlayerUpdateEvent updateEvent) {
        if (updateEvent.type != PlayerType.LOCAL)
            return;

        if (updateEvent.stage == Stage.PRE)
            clicks = 0;

        if (updateEvent.stage == Stage.POST && clickEvent.get() == ClickEvent.PRE_UPDATE)
            attemptClick();
    }

    @EventHandler
    public final void onPreAttack(PreAttackEvent preAttackEvent) {
        if (clickEvent.get() == ClickEvent.LEGIT)
            attemptClick();
    }

    @EventHandler
    public final void onBackground(BackgroundEvent backgroundEvent) {
        if(isEnabled.get() && clickTimer.hasElapsed((long) (1000 / cps.get()))) {
            clicks++;
            clickTimer.reset();
        }
    }

    private void attemptClick() {
        if(clicks > 0 && isEnabled.get()) {
            if(onClick.get())
                clicks--;
        }
    }

    protected int clicks = 0;
    private final TimerUtil clickTimer = new TimerUtil();

    public enum ClickEvent {
        PRE_UPDATE, LEGIT;
    }

}
