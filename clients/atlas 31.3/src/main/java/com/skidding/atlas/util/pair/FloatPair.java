package com.skidding.atlas.util.pair;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

/**
 * Works very similar to {@link org.apache.commons.lang3.tuple.Pair} but internally uses primitive types instead
 * This is an abstract class that's to be inherited from
 */
public abstract class FloatPair implements Map.Entry<Float, Float>, Comparable<FloatPair>, Serializable {

    public abstract float getLeft();

    public abstract float getRight();

    public abstract void setLeft(double left);

    public abstract void setRight(double left);

    @Override
    public int compareTo(@NotNull FloatPair other) {
        return new CompareToBuilder().append(this.getLeft(), other.getLeft())
                .append(this.getRight(), other.getRight())
                .toComparison();
    }

    @Override
    public Float getKey() {
        return getLeft();
    }

    @Override
    public Float getValue() {
        return getRight();
    }

    @Override

    public Float setValue(Float value) {
        final float oldValue = getRight();

        setRight(value);

        return oldValue;
    }
}
