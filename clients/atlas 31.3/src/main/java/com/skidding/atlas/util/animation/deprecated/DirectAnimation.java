package com.skidding.atlas.util.animation.deprecated;

import lombok.Getter;
import lombok.Setter;

/**
 * An animation that moves directly from one value to another
 * Deprecated, please use {@link com.skidding.atlas.util.animation.Animation} instead
 */
@Setter
@Getter
@Deprecated
public class DirectAnimation {

	private double value, lastTarget;
	private float speed;

	public DirectAnimation(double value, float speed) {
		this.value = value;
		this.speed = speed;
		this.lastTarget = value;
	}

	public void interpolate(double target) {
		if (value == target)
			return;
		this.value = AnimationUtil.move(target, value, AnimationUtil.delta, speed);
	}

	public float getValueF() {
		return (float) value;
	}

	public boolean isFinished() {
		return this.value == this.lastTarget;
	}

}