package net.jezevcik.argon.screen;

import net.jezevcik.argon.renderer.UiBuilder;

public interface ScreenCalls {

    boolean run(ScreenCall screenCall);

    enum CallType {
        DRAW_SCREEN, MOUSE_CLICK, MOUSE_RELEASE, KEY_TYPED, CHAR_TYPED;
    }

    enum MouseAction {
        PRESS, RELEASE;
    }

    record ScreenCall(CallType call, UiBuilder uiBuilder, float delta, double mouseX, double mouseY, int key, int scanCode, char chr, int modifiers) {

        public ScreenCall(UiBuilder uiBuilder, float delta, double mouseX, double mouseY) {
            this(CallType.DRAW_SCREEN, uiBuilder, delta, mouseX, mouseY, -1, -1, ' ', -1);
        }

        public ScreenCall(MouseAction mouseAction, double mouseX, double mouseY, int button) {
            this(mouseAction == MouseAction.PRESS ? CallType.MOUSE_CLICK : CallType.MOUSE_RELEASE,
                    UiBuilder.NO_DRAW, -1, mouseX, mouseY, button, -1, ' ', -1);
        }

        public ScreenCall(char character, int modifiers) {
            this(CallType.CHAR_TYPED, UiBuilder.NO_DRAW, -1, -1, -1, -1, -1, character, modifiers);
        }

        public ScreenCall(int keyCode, int scanCode, int modifiers) {
            this(CallType.KEY_TYPED, UiBuilder.NO_DRAW, -1, -1, -1, keyCode, scanCode, ' ', modifiers);
        }

    }

}
