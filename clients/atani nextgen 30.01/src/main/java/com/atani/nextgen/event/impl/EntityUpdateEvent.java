package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityUpdateEvent extends Event {
    public float prevRotationYawHead;
}
