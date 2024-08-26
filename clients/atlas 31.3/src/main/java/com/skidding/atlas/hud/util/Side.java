package com.skidding.atlas.hud.util;

import lombok.RequiredArgsConstructor;

public record Side(Horizontal horizontal,
                   Vertical vertical) {

    public static Side defaultSide() {
        return new Side(Horizontal.LEFT, Vertical.UP);
    }

    @RequiredArgsConstructor
    public enum Horizontal {
        LEFT("Left"), MIDDLE_LEFT("Middle left"), MIDDLE_RIGHT("Middle right"), RIGHT("Right");

        final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    public enum Vertical {
        UP("Up"),  MIDDLE("Middle"), DOWN("Down");

        final String name;

        @Override
        public String toString() {
            return name;
        }
    }

}
