package com.skidding.atlas.event.impl.player.chat;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatEvent extends Event {
    public String message;
}
