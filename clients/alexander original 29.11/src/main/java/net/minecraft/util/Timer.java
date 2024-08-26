package net.minecraft.util;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.game.TimeEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.security.Key;

public class Timer
{
    /**
     * How many full ticks have turned over since the last call to updateTimer(), capped at 10.
     */
    public int elapsedTicks;
    public float partialTicks;
    public float field_194148_c;
    public float renderPartialTicks;

    /**
     * The time reported by the system clock at the last sync, in milliseconds
     */
    private long lastSyncSysClock;
    private float field_194149_e;
    public float timerSpeed;

    public Timer(float tps)
    {
        this.field_194149_e = 1000.0F / tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.timerSpeed = 1.0F;
    }

    /**
     * Updates all fields of the Timer using the current time
     */
    public void updateTimer()
    {
        TimeEvent timerManipulationEvent = new TimeEvent(Minecraft.getSystemTime());
        Modification.INSTANCE.getBus().post(timerManipulationEvent);
        if(timerManipulationEvent.isFreeze()) {
            try {
                Thread.sleep(timerManipulationEvent.getFreezeTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long i = timerManipulationEvent.getTime();
        this.field_194148_c = (float)(i - this.lastSyncSysClock) / this.field_194149_e * this.timerSpeed;
        this.lastSyncSysClock = i;
        this.partialTicks += this.field_194148_c;
        this.elapsedTicks = (int)this.partialTicks;
        this.partialTicks -= (float)this.elapsedTicks;
        this.renderPartialTicks = this.partialTicks;
    }
}
