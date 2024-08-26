package com.skidding.atlas.hud;

import com.google.gson.JsonObject;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.hud.util.Side;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public abstract class HUDFactory implements Feature {

    public final String name, description;

    public abstract HUDElement build(String name, float x, float y, int priority, Side side);

    protected Supplier<Boolean> enabled = () -> true;

    public <T extends HUDFactory> T setEnabled(Supplier<Boolean> enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public JsonObject serialize() { return null; }

    @Override
    public void deserialize(JsonObject jsonObject) { }

}
