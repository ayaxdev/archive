package com.atani.nextgen.component;

import com.atani.nextgen.event.impl.BackgroundEvent;
import com.atani.nextgen.event.impl.ToClickEvent;
import com.atani.nextgen.event.impl.UpdateEvent;
import com.atani.nextgen.util.minecraft.MinecraftClient;
import de.florianmichael.rclasses.math.timer.MSTimer;
import io.github.racoondog.norbit.EventHandler;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class AutoClickerComponent implements MinecraftClient {

    private final Supplier<Boolean> isEnabled;
    private final Supplier<Float> cps;
    private final Runnable onClick;

    public AutoClickerComponent(Supplier<Boolean> isEnabled, Supplier<Float> cps) {
        this(isEnabled, cps, mc::clickMouse);
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
    public final void onClick(ToClickEvent toClickEvent) {
        if(clicks > 0 && isEnabled.get()) {
            onClick.run();

            clicks--;
        }
    }

}
