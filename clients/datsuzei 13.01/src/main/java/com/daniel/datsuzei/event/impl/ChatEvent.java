package com.daniel.datsuzei.event.impl;

import com.daniel.datsuzei.event.Event;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatEvent extends Event {
    public final String message;
}
