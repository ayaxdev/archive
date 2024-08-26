package com.skidding.atlas.command;

import com.google.gson.JsonObject;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.util.minecraft.chat.ChatUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Command implements Feature {

    public final String name, description, usage;
    public final String[] triggers;

    public void run(String[] args) {
        if(!execute(args)) {
            ChatUtil.print("Command failed:");
            ChatUtil.print(STR."Usage: .\{name} \{usage}");
            ChatUtil.print(STR."Triggers: \{String.join(", ", triggers)}");
            ChatUtil.print(STR."Description: \{description}");
        }
    }

    protected abstract boolean execute(String[] args);

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
