package com.skidding.atlas.util.animation.impl;


import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;

public class DecelerateAnimation extends Animation {

    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }


    public double getProgress(double x) {
        return 1 - ((x - 1) * (x - 1));
    }
}