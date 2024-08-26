package com.skidding.atlas.util.pair.impl.simple;

import com.skidding.atlas.util.pair.FloatPair;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutableFloatPair extends FloatPair {

    public float left;
    public float right;

    public MutableFloatPair(float left, float right) {
        this.left = left;
        this.right = right;
    }


    @Override
    public void setLeft(double left) {
        this.left = (float) left;
    }

    @Override
    public void setRight(double left) {
        this.left = (float) left;
    }
}
