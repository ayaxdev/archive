package com.skidding.atlas.component;

import com.skidding.atlas.event.impl.client.BackgroundEvent;
import com.skidding.atlas.event.impl.input.mouse.WaitForClickEvent;
import com.skidding.atlas.event.impl.player.update.UpdateEvent;
import com.skidding.atlas.util.minecraft.IMinecraft;
import de.florianmichael.rclasses.math.timer.MSTimer;
import io.github.racoondog.norbit.EventHandler;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class AutoClickerComponent implements IMinecraft {

    private final Supplier<Boolean> isEnabled;
    private final Supplier<Float> cps;
    private final Supplier<Boolean> onClick;

    public AutoClickerComponent(Supplier<Boolean> isEnabled, Supplier<Float> cps) {
        this(isEnabled, cps, () -> {
            mc.clickMouse();
            return true;
        });
    }

    protected int clicks = 0;
    private final MSTimer clickTimer = new MSTimer();

    @EventHandler
    public final void onBackground(BackgroundEvent backgroundEvent) {
        if(isEnabled.get() && clickTimer.hasReached((long) (1000 / cps.get()))) {
            clicks++;
            clickTimer.reset();
        }
    }

    @EventHandler
    public final void onUpdate(UpdateEvent updateEvent) {
        clicks = 0;
    }

    @EventHandler
    public final void onClick(WaitForClickEvent waitForClickEvent) {
        if(clicks > 0 && isEnabled.get()) {
            if(onClick.get())
                clicks--;
        }
    }

}
