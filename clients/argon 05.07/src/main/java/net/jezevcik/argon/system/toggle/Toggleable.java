package net.jezevcik.argon.system.toggle;

import net.jezevcik.argon.ParekClient;

/**
 * Classes that implement this interface can be in some way enabled and disabled.
 */
public interface Toggleable {

    /**
     * Switches the toggle state
     */
    default void toggle() {
        try {
            setEnabled(!isEnabled());
        } catch (RedundantCallException e) {
            ParekClient.LOGGER.error("RedundantCallException thrown by toggle(), this should not be possible", e);
        }
    }

    /**
     * Sets the toggle state to the provided value
     *
     * @param enabled New state
     */
    default void setEnabled(boolean enabled) throws RedundantCallException {
        if (enabled == isEnabled())
            throw new RedundantCallException(this, enabled);

        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    /**
     * Enables
     */
    void enable() throws RedundantCallException;

    /**
     * Disables
     */
    void disable() throws RedundantCallException;

    /**
     * @return The toggle state
     */
    boolean isEnabled();

}
