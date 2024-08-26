package com.daniel.datsuzei.event.impl;

import com.daniel.datsuzei.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RotationEvent extends Event {
    public float yaw, pitch;
}
