package com.daniel.datsuzei.event.impl;

import com.daniel.datsuzei.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KeyPressEvent extends Event {
    private int key;
}
