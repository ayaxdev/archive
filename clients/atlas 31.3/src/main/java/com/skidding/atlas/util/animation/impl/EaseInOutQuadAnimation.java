package com.skidding.atlas.util.animation.impl;

import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;

public class EaseInOutQuadAnimation extends Animation {

    public EaseInOutQuadAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseInOutQuadAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    public double getProgress(double x) {
        return x < 0.5 ? 2 * Math.pow(x, 2) : 1 - Math.pow(-2 * x + 2, 2) / 2;
    }

}