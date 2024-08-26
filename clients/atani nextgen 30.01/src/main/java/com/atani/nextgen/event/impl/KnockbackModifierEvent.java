package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KnockbackModifierEvent extends Event {
    public boolean flag;
    public float rotationYaw;
}
