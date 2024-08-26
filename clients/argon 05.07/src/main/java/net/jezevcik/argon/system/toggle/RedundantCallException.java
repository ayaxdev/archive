package net.jezevcik.argon.system.toggle;

import net.jezevcik.argon.system.identifier.Identifiables;

/**
 * Thrown if a call attempted to enable an already enabled Toggleable interface, or disable an already disabled one.
 */
public class RedundantCallException extends Exception {

    /**
     * Thrown if a call attempted to enable an already enabled Toggleable interface, or disable an already disabled one.
     */
    public RedundantCallException(Object object, boolean state) {
        super(String.format("Object %s is already %s!", Identifiables.getIdentifier(object), state ? "enabled" : "disabled"));
    }

}
