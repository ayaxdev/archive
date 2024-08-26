package com.skidding.atlas.util.pair.impl.simple;

import com.skidding.atlas.util.pair.FloatPair;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImmutableFloatPair extends FloatPair {

    public final float left;
    public final float right;

    public ImmutableFloatPair(float left, float right) {
        this.left = left;
        this.right = right;
    }


    @Override
    public void setLeft(double left) {
        throw new UnsupportedOperationException("ImmutableFloatPair cannot be modified");
    }

    @Override
    public void setRight(double left) {
        throw new UnsupportedOperationException("ImmutableFloatPair cannot be modified");
    }
}
