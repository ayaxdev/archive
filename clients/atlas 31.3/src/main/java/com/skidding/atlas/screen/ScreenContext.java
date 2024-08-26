package com.skidding.atlas.screen;

public record ScreenContext(Event event, int mouseX, int mouseY, int mouseButton, int key, char character) {

    public enum Event {
        ON_KEY,
        ON_DRAW,
        ON_CLICK,
    }

}
