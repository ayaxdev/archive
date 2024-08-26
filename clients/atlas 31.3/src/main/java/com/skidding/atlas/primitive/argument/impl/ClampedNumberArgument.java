package com.skidding.atlas.primitive.argument.impl;

import com.skidding.atlas.primitive.argument.Argument;

public class ClampedNumberArgument extends Argument {

    public final float min, max;

    public ClampedNumberArgument(String name, int index, float min, float max, Class<? extends Number> type) {
        super(name, index, type);

        this.min = min;
        this.max = max;
    }

}
