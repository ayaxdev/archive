package lord.daniel.alexander.util.animation;

import net.minecraft.client.Minecraft;

public class MutableAnimation {

	private double value;
	private double lastTarget;
	private float speed;

	public MutableAnimation(double value, float speed) {
		this.value = value;
		this.speed = speed;
	}

	public void interpolate(double target) {
		if (value == target)
			return;
		this.value = move(target, value, AnimationUtil.delta, speed);
	}

	public double getValue() {
		return value;
	}

	public float getValueF() {
		return (float) value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public boolean isFinished() {
		return this.value == this.lastTarget;
	}

	public double move(double target, double current, long delta, float speed) {
		lastTarget = target;
		
		if (delta < 1)
			delta = 1;

		double diff = target - current;

		boolean dir = target > current;

		current += (diff / 50) * (delta * speed) + (dir ? 0.001 : -0.001);
		if (dir)
			if (current > target)
				current = target;
		if (!dir)
			if (current < target)
				current = target;
		return current;
	}

}