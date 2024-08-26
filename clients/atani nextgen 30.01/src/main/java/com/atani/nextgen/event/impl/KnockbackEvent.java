package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KnockbackEvent extends Event {
    public float rotationYaw;
}
