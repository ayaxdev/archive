package com.skidding.atlas.util.animation.impl;

import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;

public class EaseBackInAnimation extends Animation {
    private final float easeAmount;

    public EaseBackInAnimation(int ms, double endPoint, float easeAmount) {
        super(ms, endPoint);
        this.easeAmount = easeAmount;
    }

    public EaseBackInAnimation(int ms, double endPoint, float easeAmount, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = easeAmount;
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    public double getProgress(double x) {
        float shrink = easeAmount + 1;
        return Math.max(0, 1 + shrink * Math.pow(x - 1, 3) + easeAmount * Math.pow(x - 1, 2));
    }

}