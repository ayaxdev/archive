package com.skidding.atlas.processor;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.util.minecraft.IMinecraft;

public abstract class Processor implements IMinecraft {

    public void init() {
        AtlasClient.getInstance().eventPubSub.subscribe(this);
    }

}
