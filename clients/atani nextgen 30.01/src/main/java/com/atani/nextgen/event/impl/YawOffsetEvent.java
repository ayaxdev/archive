package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class YawOffsetEvent extends Event {
    public float rotationYaw;
}
