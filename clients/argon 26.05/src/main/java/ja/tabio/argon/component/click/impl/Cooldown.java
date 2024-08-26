package ja.tabio.argon.component.click.impl;

import ja.tabio.argon.component.click.ClickMethod;

public class Cooldown extends ClickMethod {

    @Override
    public int getClicks(double target) {
        assert mc.player != null;

        if (mc.player.getAttackCooldownProgress(0.25F) < 1.0F)
            return 0;
        else
            return 1;
    }

    @Override
    public void update(double target) {
        // nuffin here
    }

    @Override
    public void reset(double target) {
        // nuffin here
    }
}