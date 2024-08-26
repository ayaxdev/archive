package lord.daniel.alexander.util.animation;

import lombok.Getter;
import lombok.Setter;

/**
 * Written by Daniel. on 02/10/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class MutableAnimation {

	@Getter
	private double value;
	private double lastTarget;
	@Getter @Setter
	private float speed;

	public MutableAnimation(double value, float speed) {
		this.value = value;
		this.speed = speed;
	}

	public void interpolate(double target) {
		if (value == target)
			return;
		lastTarget = target;
		this.value = AnimationUtil.move(target, value, AnimationUtil.delta, speed);
	}

	public float getValueF() {
		return (float) value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public boolean isFinished() {
		return this.value == this.lastTarget;
	}

}