package com.skidding.atlas.module;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public enum ModuleCategory implements Supplier<String> {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    RENDER("Render"),
    CRASHER("Crasher"),
    CHAT("Chat"),
    HUD("HUD");

    public final String name;

    @Override
    public String get() {
        return name;
    }
}