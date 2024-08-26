package com.skidding.atlas.util.animation.impl;

import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;

public class SmoothStepAnimation extends Animation {

    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    public double getProgress(double x) {
        return -2 * Math.pow(x, 3) + (3 * Math.pow(x, 2));
    }

}