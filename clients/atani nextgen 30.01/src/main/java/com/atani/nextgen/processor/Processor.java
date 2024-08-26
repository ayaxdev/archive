package com.atani.nextgen.processor;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.event.Event;
import com.atani.nextgen.util.minecraft.MinecraftClient;

public abstract class Processor implements MinecraftClient {

    public void init() {
        AtaniClient.getInstance().eventPubSub.subscribe(this);
    }

}
