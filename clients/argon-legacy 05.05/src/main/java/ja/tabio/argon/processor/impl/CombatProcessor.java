package ja.tabio.argon.processor.impl;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.Event;
import ja.tabio.argon.event.impl.AttackEvent;
import ja.tabio.argon.event.impl.BackgroundEvent;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.utils.math.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class CombatProcessor extends Processor {

    private final TimerUtil targetResetTimer = new TimerUtil();
    public Entity target;

    @EventHandler
    public final void onAttack(final AttackEvent attackEvent) {
        this.targetResetTimer.reset();
        this.target = attackEvent.entity;
    }

    @EventHandler
    public final void onBackground(final BackgroundEvent backgroundEvent) {
        if (targetResetTimer.hasElapsed(1000)) {
            target = null;
        }
    }

    public static class ValidEntityEvent extends Event {
        public final EntityPlayer entity;
        public boolean valid = true;

        public ValidEntityEvent(EntityPlayer entity) {
            this.entity = entity;
        }
    }

}
