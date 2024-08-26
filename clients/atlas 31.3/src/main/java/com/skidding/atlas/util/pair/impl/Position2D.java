package com.skidding.atlas.util.pair.impl;

import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.pair.impl.simple.MutableFloatPair;
import net.minecraft.client.gui.ScaledResolution;

public class Position2D extends MutableFloatPair implements IMinecraft {

    public Position2D(float x, float y) {
        super(x, y);
    }

    public float getX() {
        return getLeft();
    }

    public float getY() {
        return getRight();
    }

    public float getInvertedX() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledWidth() - getLeft();
    }

    public float getInvertedY() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledHeight() - getRight();
    }

    public float getCenterX() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledWidth() / 2f - getLeft();
    }

    public float getCenterY() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.getScaledHeight() / 2f - getRight();
    }
}
