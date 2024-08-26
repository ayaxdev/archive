package com.skidding.atlas.util.animation.impl;

import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;

public class EaseOutSineAnimation extends Animation {


    public EaseOutSineAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseOutSineAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    public double getProgress(double x) {
        return Math.sin(x * (Math.PI / 2));
    }
}