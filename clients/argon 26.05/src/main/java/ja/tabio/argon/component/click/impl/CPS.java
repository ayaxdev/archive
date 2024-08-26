package ja.tabio.argon.component.click.impl;

import ja.tabio.argon.component.click.ClickMethod;

public class CPS extends ClickMethod {

    private long time = System.currentTimeMillis();
    private int last = 0;

    @Override
    public int getClicks(double target) {
        return last = (int) Math.round((System.currentTimeMillis() - time) / (1000.0 / target));
    }

    @Override
    public void update(double target) {
        time += (long) (last * (1000.0 / target));
    }

    @Override
    public void reset(double target) {
        time = (long) (System.currentTimeMillis() - (1000 / target));
    }

}
