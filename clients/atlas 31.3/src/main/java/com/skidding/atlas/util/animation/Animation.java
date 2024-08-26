package com.skidding.atlas.util.animation;

import com.skidding.atlas.util.system.TimerUtil;
import lombok.Getter;

/**
 * An animation superclass
 */
public abstract class Animation {

    /**
     * Timer for the animation progress
     */
    public TimerUtil progressTimer = new TimerUtil();

    /**
     * Time in milliseconds of how long you want the animation to take
     */
    public int duration;

    /**
     * The desired distance for the animated output to go
     */
    public double endPoint;

    /**
     * The Direction in which the animation is going. If backwards, will start from endPoint and go to 0.
     */
    @Getter
    protected Direction direction;

    /**
     * Immutable animation, you cannot change the endPoint or duration
     *
     * @param ms Time in milliseconds of how long you want the animation to take
     * @param endPoint The desired distance for the animated object to go
     */
    public Animation(int ms, double endPoint) {
        this(ms, endPoint, Direction.FORWARDS);
    }

    /**
     * Immutable animation, you cannot change the endPoint or duration
     *
     * @param ms Time in milliseconds of how long you want the animation to take
     * @param endPoint The desired distance for the animated object to go
     * @param direction The Direction in which the animation is going. If backwards, will start from endPoint and go to 0.
     */
    public Animation(int ms, double endPoint, Direction direction) {
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = direction;
    }


    /**
     *  Checks if the animation is finished
     *
     * @param direction The direction of the animation
     * @return true if the animation is finished
     */
    public boolean finished(Direction direction) {
        return isDone() && this.direction.equals(direction);
    }

    /**
     * Returns the linear output of the animation
     *
     * @return The linear output
     */
    public double getLinearOutput() {
        return 1 - ((progressTimer.getTime() / (double) duration) * endPoint);
    }

    /**
     * Resets the animation
     */
    public void reset() {
        progressTimer.reset();
    }

    /**
     * Checks if the animation is done
     *
     * @return true if the animation is done
     */
    public boolean isDone() {
        return progressTimer.hasElapsed(duration);
    }

    /**
     * Changes the direction of the animation
     */
    public void changeDirection() {
        setDirection(direction.opposite());
    }

    /**
     * Sets the direction of the animation
     *
     * @param direction The desired direction of the animation
     */
    public Animation setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            progressTimer.setLastMS(System.currentTimeMillis() - (duration - Math.min(duration, progressTimer.getTime())));
        }
        return this;
    }

    /**
     * Checks whether the output should be corrected.
     * This is meant to be overridden.
     *
     * @return true if the output should be corrected
     */

    protected boolean correctOutput() {
        return false;
    }

    /**
     * Gets the output of the animation
     *
     * @return The output of the animation
     */
    public double getOutput() {
        if (direction.forwards()) {
            if (isDone()) {
                return endPoint;
            }

            return getProgress(progressTimer.getTime() / (double) duration) * endPoint;
        } else {
            if (isDone()) {
                return 0.0;
            }

            if (correctOutput()) {
                double revTime = Math.min(duration, Math.max(0, duration - progressTimer.getTime()));
                return getProgress(revTime / (double) duration) * endPoint;
            }

            return (1 - getProgress(progressTimer.getTime() / (double) duration)) * endPoint;
        }
    }

    /**
     * This is where the animation equation should go, for example, a logistic function.
     *
     * @param x The animation time
     * @return The output ranging from 0-1.
     */
    public abstract double getProgress(double x);

}